<template>
  <div
      class="user-avatar"
      :style="avatarStyle"
      @click="handleAvatarClick"
  >
    {{ avatarInitial }}
  </div>
</template>

<script setup>
import { computed } from 'vue';

const props = defineProps({
  userAvatar: {
    type: String,
    default: ''
  },
  userDisplayName: {
    type: String,
    default: ''
  },
  avatarSize: {
    type: Number,
    default: 60
  }
});

const emit = defineEmits(['avatarClick']);

const avatarInitial = computed(() => {
  return props.userAvatar ? '' : props.userDisplayName?.charAt(0).toUpperCase() || '?';
});

const avatarStyle = computed(() => {
  const size = props.avatarSize;
  if (props.userAvatar) {
    // 有头像时, 显示图片
    return {
      backgroundImage: `url(${props.userAvatar})`,
      backgroundSize: 'cover',
      backgroundPosition: 'center',
      width: `${size}px`,
      height: `${size}px`,
    };
  } else {
    // 无头像时背景颜色配置
    return {
      background: 'linear-gradient(to top, #fd4500, rgba(253, 69, 0, 0.7))',
      color: 'white',
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'center',
      fontWeight: 'bold',
      width: `${size}px`,
      height: `${size}px`,
    };
  }
});

const handleAvatarClick = () => {
  emit('avatarClick');
};
</script>

<style scoped>
.user-avatar {
  border-radius: 50%;
  overflow: hidden;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  transition: transform 0.2s ease;
  cursor: pointer;
  user-select: none;
  font-size: 1.1rem;
}

.user-avatar:hover {
  transform: scale(1.05);
}
</style>
