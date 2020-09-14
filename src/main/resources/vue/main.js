import Vue from 'vue'
import App from './App.vue'
import router from './router'

import VueNativeSock from 'vue-native-websocket'

Vue.config.productionTip = false
Vue.use(VueNativeSock, 'ws://localhost:9000/ws')

new Vue({
  router,
  render: h => h(App)
}).$mount('#app')
