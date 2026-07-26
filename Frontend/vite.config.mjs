import { fileURLToPath, URL } from 'node:url'
import Vue from '@vitejs/plugin-vue'
// Plugins
import AutoImport from 'unplugin-auto-import/vite'
import Fonts from 'unplugin-fonts/vite'
import Components from 'unplugin-vue-components/vite'
import { VueRouterAutoImports } from 'unplugin-vue-router'
import VueRouter from 'unplugin-vue-router/vite'
// Utilities
import { defineConfig } from 'vite'

import Layouts from 'vite-plugin-vue-layouts-next'
import Vuetify, { transformAssetUrls } from 'vite-plugin-vuetify'

// https://vitejs.dev/config/
export default defineConfig({
    plugins: [
        VueRouter(),
        Layouts(),
        Vue({
            template: { transformAssetUrls },
        }),
        // https://github.com/vuetifyjs/vuetify-loader/tree/master/packages/vite-plugin#readme
        Vuetify({
            autoImport: true,
            styles: {
                configFile: 'src/styles/settings.scss',
            },
        }),
        Components(),
        Fonts({
            google: {
                families: [{
                    name: 'Roboto',
                    styles: 'wght@100;300;400;500;700;900',
                }],
            },
        }),
        AutoImport({
            imports: [
                'vue',
                VueRouterAutoImports,
                {
                    pinia: ['defineStore', 'storeToRefs'],
                },
            ],
            eslintrc: {
                enabled: true,
            },
            vueTemplate: true,
        }),
    ],
    server: {
        port: 3002,
        strictPort: true,
        proxy: {
            '/api': {
                target: 'http://localhost:8080',
                changeOrigin: true,
                secure: false,
            },
            // Proxy Nominatim requests to avoid CORS errors in development
            '/nominatim': {
                target: 'https://nominatim.openstreetmap.org',
                changeOrigin: true,
                secure: true,
                headers: {
                    // Nominatim requires a valid User-Agent and optionally a contact email
                    'User-Agent': 'DATN-PeakSeven-ShopGiay-Dev/1.0 (contact@peakseven.com)',
                    'From': 'contact@peakseven.com'
                },
                rewrite: (path) => path.replace(/^\/nominatim/, ''),
            },
            '/images': {
                target: 'http://localhost:8080',
                changeOrigin: true,
                secure: false,
            },
        },
    },
    resolve: {
        alias: {
            '@': fileURLToPath(new URL('src',
                import.meta.url)),
        },
    },
})