from django.urls import path
from channels.routing import ProtocolTypeRouter, URLRouter
from .consumer import ChatConsumer, UserConsumer, CustomConsumer

websockets = URLRouter([
    path(
        "ws/chat/<int:chat_id>", ChatConsumer,
        name="life-chat",
    ),
    path(
        "ws/user/<int:user_id>", UserConsumer,
        name="life-user",
    ),
    path(
        "ws/custom/<int:chat_id>", CustomConsumer,
        name="life-custom",
    ),
])



