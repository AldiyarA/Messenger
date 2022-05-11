import self as self
from rest_framework import generics, permissions, status
from rest_framework.response import Response
from rest_framework.views import APIView
from core.serializers import ChatSerializer, MessageResponseSerializer, ChatMessageSerializer
from core.models import Chat, Message, UserChat
import logging

logger = logging.getLogger(__name__)


class ChatListAPIView(generics.ListCreateAPIView):
    permission_classes = [permissions.IsAuthenticatedOrReadOnly]
    queryset = Chat.objects.all()
    serializer_class = ChatSerializer

    def get(self, request, *args, **kwargs):
        serializer = self.serializer_class(self.queryset.all(), many=True)
        logger.info('chat list')
        return Response(serializer.data, status=status.HTTP_200_OK)


class ChatDetailAPIView(generics.RetrieveUpdateDestroyAPIView):
    permission_classes = [permissions.IsAuthenticatedOrReadOnly]
    queryset = Chat.objects.all()
    serializer_class = ChatSerializer


class CharMessagesAPIView(APIView):
    permission_classes = [permissions.IsAuthenticatedOrReadOnly]

    def get(self, request, chat_id):
        messages = Message.objects.filter(chat=chat_id).order_by('id')
        serializer = MessageResponseSerializer(messages, many=True)
        for message in serializer.data:
            print(message['sender']['id'], "?=?", self.request.user.id, "is",
                  (message['sender']['id'] == self.request.user.id))
            message['is_sender'] = (message['sender']['id'] == self.request.user.id)
            print(message['is_sender'])
        return Response(serializer.data, status=status.HTTP_200_OK)
