from rest_framework import serializers
from user.models import Profile
from django.conf import settings


# class ProfileSerializer(serializers.Serializer):
#     id = serializers.IntegerField(read_only=True)
#     user_id = serializers.IntegerField(read_only=True)
#     first_name = serializers.CharField()
#     last_name = serializers.CharField()
#     bio = serializers.CharField()
#     phone = serializers.CharField()


class ProfileSerializer(serializers.ModelSerializer):
    # user_id = serializers.IntegerField(read_only=True)

    class Meta:
        model = Profile
        # fields = ['first_name', 'last_name', 'bio', 'phone', 'user_id']
        fields = '__all__'
