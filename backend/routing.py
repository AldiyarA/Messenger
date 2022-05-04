from channels.routing import ProtocolTypeRouter, URLRouter
from core.routing import websockets
from core.middlewares import TokenAuthMiddleware


application = ProtocolTypeRouter({
    "websocket": TokenAuthMiddleware(websockets),
})
