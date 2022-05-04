from django.db import models


class UserChatManager(models.Manager):
    def get_users(self, chat_id):
        users = [i.user for i in self.get_queryset().filter(chat=chat_id)]
        return users

    def get_chats(self, user_id):
        chats = [i.chat for i in self.get_queryset().filter(user=user_id)]
        return chats