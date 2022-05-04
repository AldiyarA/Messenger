from django.db import models
from django.conf import settings
from core.managers import UserChatManager


class Chat(models.Model):
    name = models.CharField(max_length=50)
    description = models.CharField(max_length=250, default="", blank=True)
    owner = models.ForeignKey(settings.AUTH_USER_MODEL, on_delete=models.CASCADE)

    def message(self):
        messages = self.messages.all()
        if len(messages) == 0:
            return None
        return messages.last()


class MessageType(models.Model):
    name = models.CharField(max_length=50)


class Message(models.Model):
    content = models.CharField(max_length=1000)
    creator = models.ForeignKey(settings.AUTH_USER_MODEL, on_delete=models.CASCADE, related_name='created_messages')
    sender = models.ForeignKey(settings.AUTH_USER_MODEL, on_delete=models.CASCADE, related_name='sent_messages')
    date = models.DateTimeField(auto_now_add=True, blank=True)
    chat = models.ForeignKey(Chat, on_delete=models.CASCADE, blank=True, default=3, related_name='messages')
    message_type = models.ForeignKey(MessageType, on_delete=models.CASCADE, default=1, blank=True)


class UserChat(models.Model):
    objects = UserChatManager()
    user = models.ForeignKey(settings.AUTH_USER_MODEL, on_delete=models.CASCADE, related_name='chats')
    chat = models.ForeignKey(Chat, on_delete=models.CASCADE, related_name='users')

    class Meta:
        unique_together = ('user', 'chat',)

