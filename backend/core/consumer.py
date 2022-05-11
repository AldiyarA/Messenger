import json
# Импорты сторонних библиотек.
from channels.exceptions import DenyConnection
from channels.generic.websocket import AsyncWebsocketConsumer
# Импорты Django.
from django.contrib.auth.models import AnonymousUser
# Локальные импорты.
from core.models import Message, MessageType, UserChat
from core.serializers import MessageSerializer, MessageResponseSerializer
import logging

logger = logging.getLogger(__name__)


class ChatConsumer(AsyncWebsocketConsumer):
    async def connect(self):

        logger.info("Connect 1")
        chat_id = self.scope['url_route']['kwargs']['chat_id']

        user = self.scope['user']
        if user == AnonymousUser():
            logger.info("No such user")
            raise DenyConnection("Authentication error")
        else:
            logger.info(str(user))
        logger.info("Connect 2")

        self.room_name = chat_id
        self.room_group_name = f'Chat_{chat_id}_{user}'
        logger.info("Connect 3")
        await self.channel_layer.group_add(
            self.room_group_name,
            self.channel_name
        )
        logger.info("Connect 4")
        try:
            UserChat.objects.get(user=self.scope['user'], chat=chat_id)
            self.chat = chat_id
        except UserChat.DoesNotExist:
            raise DenyConnection("No such user in this chat")
        logger.info("Connect 5")
        await self.accept()
        logger.info("Connect 6")

    async def websocket_receive(self, data):
        logger.info("Receive 1" + str(data))
        try:
            j_data = json.loads(data['text'])
        except Exception:
            return
        logger.info("Receive 2")
        try:
            response_message = self.response_message(j_data)
        except Exception:
            logger.info("Some error on response_message")
            response_message = {"id": -666}
        if response_message["id"] == -666:
            return
        users = UserChat.objects.get_users(self.room_name)
        for user in users:
            await self.channel_layer.group_send(
                f'Chat_{response_message["chat"]}_{user.id}',
                {
                    'type': 'to_user',
                    'chat_id': self.room_name,
                    'data': response_message
                }
            )
        logger.info("Receive 4")

    def response_message(self, data):
        logger.info("ROOM " + str(self.room_group_name))
        m_type = data["type"]
        if m_type == "edit":
            return self.edit_message(data)
        elif m_type == "delete":
            return self.delete_message(data)
        try:
            serializer = self.__serializer(data)
        except Exception:
            logger.info("Serializer error")
            return {"id": -666}
        if serializer.is_valid():
            serializer.save()
            logger.info("New message" + str(serializer.data))
            res_serializer = MessageResponseSerializer(Message.objects.get(id=serializer.data['id']))
            res_data = res_serializer.data
            logger.info("res_data", res_data)
            return res_data
        else:
            logger.info("invalid data")
            return {"id": -666}

    def edit_message(self, data):
        msg = Message.objects.get(pk=data["message"])
        if msg.message_type == 2 or msg.sender.id != self.scope['user']:
            logger.info("Edit error")
            return {"id": -666}
        msg.content = data["content"]
        msg.message_type = MessageType.objects.get(pk=3)
        msg.save()
        res_serializer = MessageResponseSerializer(msg)
        res_data = res_serializer.data
        return res_data

    def delete_message(self, data):
        msg = Message.objects.get(pk=data["message"])
        if msg.sender.id != self.scope['user']:
            logger.info("Delete error")
            return {"id": -666}
        res_serializer = MessageResponseSerializer(msg)
        res_data = res_serializer.data
        msg.delete()
        res_data['message_type'] = 4
        return res_data

    def __serializer(self, data):
        sender = self.scope['user']
        m_type = data["type"]
        new_msg = {"sender": sender}
        if m_type == "send":  # type content chat
            new_msg["content"] = data["content"]
            new_msg["creator"] = sender
            new_msg["message_type"] = 1
            new_msg["chat"] = self.chat
            return MessageSerializer(data=new_msg)
        elif m_type == "resend":  # type message chat
            UserChat.objects.get(chat_id=data["chat"], user_id=self.scope['user'])
            old_msg = Message.objects.get(pk=data["message"])
            logger.info("SERIALIZER")
            serializer = MessageSerializer(old_msg)
            logger.info(str(serializer.data))
            new_msg["content"] = old_msg.content
            new_msg["creator"] = old_msg.creator.pk
            new_msg["message_type"] = 2
            new_msg["chat"] = data["chat"]
            return MessageSerializer(data=new_msg)

    async def to_user(self, event):
        data = event['data']
        logger.info("EVENT" + str(event))
        logger.info("user" + str(self.scope['user']))
        user = self.scope['user']
        data['is_sender'] = data['sender']['id'] == user
        await self.send(text_data=json.dumps(data))

    async def websocket_disconnect(self, message):
        logger.info("Disconnect 1")
        await self.channel_layer.group_discard(
            self.room_group_name,
            self.channel_name
        )
        logger.info("Disconnect 2")


# User consumer


# User consumer


# User consumer


# User consumer


# User consumer


class UserConsumer(AsyncWebsocketConsumer):
    async def connect(self):
        print("Connect 1")
        user_id = self.scope['url_route']['kwargs']['user_id']
        self.room_name = user_id
        self.room_group_name = f'USER_{user_id}'

        print("Connect 2")
        scope_user = self.scope['user']
        if scope_user == AnonymousUser():
            DenyConnection("Просрочен токен || Отсудсвует токен || Такого пользователя не существует")
        elif scope_user != user_id:
            DenyConnection("Подключечие к другому пользавателю запрещено")
        else:
            print("USER", self.scope['user'])
            # raise DenyConnection("Такого пользователя не существует")

        print("Connect 3")
        await self.channel_layer.group_add(
            self.room_group_name,
            self.channel_name
        )
        print("Connect 4")
        await self.accept()

        print("Connect 5 FINISH")
        print("Returning already sent messages")

        chats = UserChat.objects.get_chats(self.scope['user'])

    async def websocket_receive(self, data):
        print("Preparing data 1")
        j_data = json.loads(data['text'])

        print("Preparing data 2")
        j_data = self.gen_message(j_data)
        print("j_data", j_data)

        print("Preparing data 3")
        chat = j_data['chat']
        users = UserChat.objects.get_users(chat)

        print("Preparing data 4 FINISH")
        for user in users:
            print("Send to user:", user.pk)
            room_group_name = f'USER_{user.pk}'
            await self.channel_layer.group_send(
                room_group_name,
                {
                    'type': 'chat_message',
                    'user_id': self.room_name,
                    'data': j_data
                }
            )

    def gen_message(self, data):
        print("ROOM", self.room_group_name)
        sender = self.scope['user']
        m_type = data["type"]
        print(m_type)

        new_msg = {
            "sender": sender,
        }

        if m_type == "send":  # type content chat
            new_msg["content"] = data["content"]
            new_msg["creator"] = sender
            new_msg["message_type"] = 1
            new_msg["chat"] = data["chat"]
            serializer = MessageSerializer(data=new_msg)
        elif m_type == "resend":  # type message chat
            old_msg = Message.objects.get(pk=data["message"])
            print("SERIALIZER")
            serializer = MessageSerializer(old_msg)
            print(serializer.data)
            new_msg["content"] = old_msg.content
            new_msg["creator"] = old_msg.creator.pk
            new_msg["message_type"] = 2
            new_msg["chat"] = data["chat"]
            serializer = MessageSerializer(data=new_msg)
        else:
            serializer = None
        print("new_msg", new_msg)

        # user_chat = UserChat.objects.get(user=sender, chat=data["chat"])

        if serializer.is_valid():
            print("VALIDVALIDVALIDVALIDVALIDVALIDVALIDVALIDVALIDVALIDVALIDVALIDVALID")
            serializer.save()

            res_serializer = MessageResponseSerializer(Message.objects.get(id=serializer.data['id']))
            res_data = res_serializer.data
            print("res_data", res_data)
            return res_data
        else:
            return {"status": "failed"}

    async def chat_message(self, event):
        data = event['data']
        print("EVENT", event)
        await self.send(text_data=json.dumps(data))

    async def websocket_disconnect(self, message):
        print("Disconnect 1")
        await self.channel_layer.group_discard(
            self.room_group_name,
            self.channel_name
        )
        print("Disconnect 2")


#

#

#

#

#

class CustomConsumer(AsyncWebsocketConsumer):
    async def connect(self):
        print("Connect 1")
        chat_id = self.scope['url_route']['kwargs']['chat_id']
        self.room_name = chat_id
        self.room_group_name = f'Custom_{chat_id}'
        print("Connect 2")

        print("USER", self.scope['user'])

        await self.channel_layer.group_add(
            self.room_group_name,
            self.channel_name
        )
        print("Connect 3")

        await self.accept()
        print("Connect 4")

    async def websocket_receive(self, data):
        print("Receive 1")
        print("DATA", data)
        j_data = json.loads(data['text'])
        print("Receive 2")

        await self.channel_layer.group_send(
            self.room_group_name,
            {
                'type': 'chat_message',
                'chat_id': self.room_name,
                'data': j_data
            }
        )
        print("Receive 4")

    async def chat_message(self, event):
        data = event['data']
        print("EVENT", event)

        print("Sending 1")

        await self.send(text_data=json.dumps(data))

    async def websocket_disconnect(self, message):
        print("Disconnect 1")
        await self.channel_layer.group_discard(
            self.room_group_name,
            self.channel_name
        )
        print("Disconnect 2")
