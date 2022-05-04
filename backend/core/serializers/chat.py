from rest_framework import serializers

from core.models import Chat
from .message import MessageResponseSerializer


class ChatSerializer(serializers.ModelSerializer):
    class Meta:
        model = Chat
        fields = '__all__'


class ChatMessageSerializer(serializers.ModelSerializer):
    message = MessageResponseSerializer(read_only=True)

    class Meta:
        model = Chat
        fields = ['id', 'name', 'description', 'message']
