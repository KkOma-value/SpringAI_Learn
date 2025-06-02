import { createRouter, createWebHistory } from 'vue-router'
import HomePage from '../views/HomePage.vue'
import LoveAppPage from '../views/LoveAppPage.vue'
import ManusPage from '../views/ManusPage.vue'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: HomePage
  },
  {
    path: '/loveapp',
    name: 'LoveApp',
    component: LoveAppPage
  },
  {
    path: '/manus',
    name: 'Manus', 
    component: ManusPage
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router 