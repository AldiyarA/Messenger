from django.contrib import admin
from .models import Chat, Message, MessageType, UserChat

# Register your models here.
admin.site.register(Chat)
admin.site.register(Message)
admin.site.register(MessageType)
admin.site.register(UserChat)
