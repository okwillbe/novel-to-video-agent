<script setup lang="ts">
import { RouterView } from 'vue-router'
import AppHeader from '@/components/AppHeader.vue'
</script>

<template>
  <div class="tw-min-h-screen tw-bg-gray-50">
    <AppHeader />
    <main class="tw-container tw-mx-auto tw-px-4 tw-py-6">
      <RouterView />
    </main>
  </div>
</template>
