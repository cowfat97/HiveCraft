import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: () => import('@/views/Home.vue'),
    meta: { title: 'Card 列表' }
  },
  {
    path: '/card/:id',
    name: 'CardDetail',
    component: () => import('@/views/CardDetail.vue'),
    meta: { title: 'Card 详情' }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  document.title = `${to.meta.title} - AgentHub`
  next()
})

export default router