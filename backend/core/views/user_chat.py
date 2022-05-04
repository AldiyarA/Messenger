from rest_framework.response import Response
from rest_framework import status, permissions, generics
from rest_framework.views import APIView

from core.models import UserChat
from core.serializers import ChatSerializer, ChatMessageSerializer


class UserChatListAPIView(generics.ListAPIView):
    permission_classes = [permissions.IsAuthenticated]
    manager = UserChat.objects
    serializer_class = ChatMessageSerializer

    def get(self, request, *args, **kwargs):
        serializer = self.serializer_class(self.manager.get_chats(self.request.user), many=True)
        return Response(serializer.data, status=status.HTTP_200_OK)


class UserChatAPIView(APIView):
    permission_classes = [permissions.IsAuthenticatedOrReadOnly]

    def get(self, request):
        chats = UserChat.objects.get_chats(self.request.user)
        serializer = ChatSerializer(chats, many=True)
        print(serializer.data)
        return Response(serializer.data, status=status.HTTP_200_OK)

    def post(self, request):
        userChat = UserChat(chat_id=self.request.data['chat'], user=self.request.user)
        userChat.save()
        return Response(status=status.HTTP_200_OK)
