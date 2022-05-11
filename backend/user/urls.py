from django.urls import path
from .views import create_user, custom, ProfileViewSet
from rest_framework import routers

router = routers.DefaultRouter()
router.register('profile', ProfileViewSet)
urlpatterns = router.urls

urlpatterns += [
    path('signup/', create_user),
    path('custom/', custom)
]
