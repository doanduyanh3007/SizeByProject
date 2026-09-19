import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import pinia from './stores'
import vuetify from './plugins/vuetify'
import './styles/index.css'
import vue3GoogleLogin from 'vue3-google-login'

import { useProductStore } from './stores/products'
import axios from 'axios'

// --- Global Axios Interceptor ---
axios.interceptors.request.use((config) => {
    const token = localStorage.getItem('token');
    if (token) {
        config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
});

// --- Global Fetch Interceptor ---
const originalFetch = window.fetch;
window.fetch = async function () {
    let [resource, config] = arguments;
    
    const urlStr = typeof resource === 'string' ? resource : (resource && resource.url ? resource.url : '');
    const isBackend = urlStr.includes('localhost:8080') || urlStr.startsWith('/api') || urlStr.includes('127.0.0.1:8080');

    if (isBackend) {
        const token = localStorage.getItem('token');
        if (token) {
            if (!config) config = {};
            if (!config.headers) config.headers = {};
            
            if (config.headers instanceof Headers) {
                if (!config.headers.has('Authorization')) {
                    config.headers.append('Authorization', `Bearer ${token}`);
                }
            } else if (Array.isArray(config.headers)) {
                if (!config.headers.some(([key]) => key.toLowerCase() === 'authorization')) {
                    config.headers.push(['Authorization', `Bearer ${token}`]);
                }
            } else {
                if (!config.headers['Authorization'] && !config.headers['authorization']) {
                    config.headers['Authorization'] = `Bearer ${token}`;
                }
            }
        }
    }
    return originalFetch(resource, config);
};
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