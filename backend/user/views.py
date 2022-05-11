import json

import jwt
from django.conf import settings
from django.views.decorators.csrf import csrf_exempt
from rest_framework import viewsets, mixins
from rest_framework.exceptions import AuthenticationFailed
from rest_framework.permissions import IsAuthenticated
from rest_framework.response import Response

from .models import User, Profile
from django.http import HttpResponse, JsonResponse
from user.serializers import ProfileSerializer


# Create your views here.
@csrf_exempt
def create_user(request):
    if request.method == "POST":
        data = json.loads(request.body)
        user = data['user']
        email = user['email']
        password = user['password']
        print(email, password)
        id = User.objects.create_user(email, password)
        profile_data = data['profile']
        profile = Profile.objects.get(user=id)
        if 'first_name' in profile_data:
            profile.first_name = profile_data['first_name']
        if 'last_name' in profile_data:
            profile.last_name = profile_data['last_name']
        if 'bio' in profile_data:
            profile.bio = profile_data['bio']
        if 'phone' in profile_data:
            profile.phone = profile_data['phone']
        profile.save()
        return HttpResponse(id)


@csrf_exempt
def custom(request):
    if request.method == "POST":
        print("request", request.body)
        return JsonResponse({"refresh": "a", "access": "b"})


def get_user(request):
    prefix, token = request.headers.get("Authorization").split()
    if prefix not in settings.SIMPLE_JWT['AUTH_HEADER_TYPES']:
        raise AuthenticationFailed("incorrect prefix")
    d_token = jwt.decode(token, settings.SECRET_KEY, algorithms=['HS256'])
    user_id = d_token['user_id']
    return user_id


class ProfileViewSet(viewsets.GenericViewSet, mixins.ListModelMixin):
    queryset = Profile.objects.all()
    serializer_class = ProfileSerializer
    permission_classes = [IsAuthenticated]

    def list(self, request, *args, **kwargs):
        profile = self.queryset.get(user=self.request.user)
        serializer = self.serializer_class(instance=profile)
        return Response(serializer.data)
