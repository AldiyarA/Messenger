from django.db.models.signals import post_save
from django.dispatch import receiver
from .models import Chat, UserChat


@receiver(post_save, sender=Chat)
def create_chat_user(sender, instance, created, **kwargs):
    print("Chat Signal")
    print(instance.owner)
    if created:
        UserChat.objects.create(chat=instance, user=instance.owner)
