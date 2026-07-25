<template>
  <div class="app">
    <main class="main">
      <router-view v-slot="{ Component }">
        <transition name="fade" mode="out-in"><component :is="Component" /></transition>
      </router-view>
    </main>
    <footer class="ft">
      <span>AgentHub</span>
    </footer>
  </div>
</template>

<script setup></script>

<style lang="scss">
.app { min-height:100vh; display:flex; flex-direction:column; }
.main { flex:1; padding: 48px 32px 64px; }

.ft { text-align:center; padding:20px; font-size:12px; color:var(--c-text3); letter-spacing:.02em;
  background: rgba(255,255,255,.6); backdrop-filter:blur(10px);
}

.fade-enter-active,.fade-leave-active { transition: opacity .25s ease, transform .25s ease; }
.fade-enter-from { opacity:0; transform: translateY(8px); }
.fade-leave-to { opacity:0; transform: translateY(-8px); }
</style>
