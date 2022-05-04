from rest_framework import serializers

from core.models import Message
from user.models import User, Profile


class ProfileSerializer(serializers.ModelSerializer):
    class Meta:
        model = Profile
        fields = ['first_name', 'last_name']


class UserSerializer(serializers.ModelSerializer):
    profile = ProfileSerializer(read_only=True)

    class Meta:
        model = User
        fields = ['id', 'profile']


class MessageResponseSerializer(serializers.ModelSerializer):
    creator = UserSerializer(read_only=True)
    sender = UserSerializer(read_only=True)
    is_sender = serializers.BooleanField(read_only=True)

    class Meta:
        model = Message
        fields = ['id', 'content', 'creator', 'sender', 'date', 'chat', 'message_type', 'is_sender']


class MessageSerializer(serializers.ModelSerializer):
    class Meta:
        model = Message
        fields = '__all__'
