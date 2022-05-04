from urllib.parse import urlparse, parse_qs
from channels.auth import AuthMiddlewareStack
import jwt
from channels.exceptions import DenyConnection
from django.conf import settings

# Импорт DRF.
from rest_framework.authtoken.models import Token
from rest_framework.exceptions import AuthenticationFailed

# Импорты Django.
from django.contrib.auth.models import AnonymousUser
from django.db import close_old_connections


class TokenAuthMiddleware:
    """
    Промежуточный слой для токена авторизации
    """
    def __init__(self, inner):
        self.inner = inner

    def __call__(self, scope):
        print("MIDDLEWARE 1")
        query_string = scope['query_string']
        print("SCOPE", scope)
        if query_string:
            print("MIDDLEWARE 2")
            try:
                parsed_query = parse_qs(query_string)
                token_key = parsed_query[b'token'][0].decode()
                token_name = 'token'
                print("MIDDLEWARE 3")
                if token_name == 'token':
                    # user, _ =  # Ваша функция аутентификации
                    print("MIDDLEWARE 4")
                    print(token_key)
                    try:
                        d_token = jwt.decode(token_key, settings.SECRET_KEY, algorithms=['HS256'])
                        user_id = d_token['user_id']
                        scope['user'] = user_id

                    except jwt.ExpiredSignatureError:
                        scope['user'] = AnonymousUser()

                    close_old_connections()
                    print("MIDDLEWARE 5")
            except AuthenticationFailed:
                scope['user'] = AnonymousUser()
        else:
            scope['user'] = AnonymousUser()
        return self.inner(scope)


def TokenAuthMiddlewareStack(inner):
    return TokenAuthMiddleware(AuthMiddlewareStack(inner))
