import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import pinia from './stores'
import vuetify from './plugins/vuetify'
import './styles/index.css'
import vue3GoogleLogin from 'vue3-google-login'

import { useProductStore } from './stores/products'
const app = createApp(App)

app.use(pinia)
app.use(vuetify)
app.use(router)

app.use(vue3GoogleLogin, {
  clientId: '550560076020-c21vt0n2o2dclllmib3qhvnqeh6tjqvm.apps.googleusercontent.com' 
})

const productStore = useProductStore()
productStore.initializeStore().catch(error => {
    console.error('Failed to initialize product store:', error)
})

app.mount('#app')