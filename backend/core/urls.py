from django.urls import path
from core.views import UserChatAPIView, ChatListAPIView, ChatDetailAPIView, CharMessagesAPIView, \
    MessageListAPIView, MessageDetailAPIView, UserChatListAPIView


urlpatterns = [
    path('user/chat/', UserChatAPIView.as_view()),

    path('chats/', ChatListAPIView.as_view()),
    path('user/chats/', UserChatListAPIView.as_view()),
    path('chats/<int:pk>/', ChatDetailAPIView.as_view()),
    path('chats/<int:chat_id>/messages/', CharMessagesAPIView.as_view()),

    path('messages/', MessageListAPIView.as_view()),
    path('messages/<int:pk>/', MessageDetailAPIView.as_view()),
]



