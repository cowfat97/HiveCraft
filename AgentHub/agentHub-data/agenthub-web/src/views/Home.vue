<template>
  <div class="home">
    <!-- hero -->
    <section class="hero">
      <div class="hero-brand">
        <div class="brand-icon">
          <svg width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="#fff" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"><path d="M12 2 2 7l10 5 10-5-10-5z"/><path d="M2 17 12 22l10-5"/><path d="M2 12 12 17l10-5"/></svg>
        </div>
        <span class="brand-text">AgentHub</span>
      </div>
      <div class="search-box">
        <el-input v-model="kw" placeholder="搜索 Agent 名称或描述" size="large" clearable @input="onKw" @clear="onClear" class="search-input">
          <template #prefix><el-icon><Search /></el-icon></template>
        </el-input>
      </div>
    </section>

    <!-- card stack -->
    <section class="stack-section" v-if="stack.length || loading">
      <div v-if="!stack.length && loading" class="stack-skel"></div>

      <div class="stack">
        <!-- bg cards -->
        <div v-for="(c, i) in bgCards" :key="c.id" class="sc bg"
          :style="{ zIndex: bgCards.length-i, transform: `scale(${0.95-(bgCards.length-i)*0.03}) translateY(${(bgCards.length-i)*12}px)` }">
        </div>

        <!-- top card -->
        <div v-if="topCard" class="sc top" ref="cardEl"
          :class="{ dragging, exiting, exitLeft: exitDir==='left', exitRight: exitDir==='right' }"
          :style="dragStyle"
          @pointerdown="onDown" @click="onClick">
          <div class="sc-grad"></div>
          <div class="sc-body">
            <div class="sc-tags">
              <span v-for="t in (topCard.tags||[]).slice(0,3)" :key="t" class="tag">{{ t }}</span>
            </div>
            <h2 class="sc-name">{{ topCard.name }}</h2>
            <p class="sc-desc">{{ topCard.summary || topCard.description || '暂无简介' }}</p>
            <div class="sc-meta">
              <span class="sc-stat"><el-icon><View /></el-icon> {{ topCard.viewCount||0 }}</span>
              <span class="sc-stat"><el-icon><Star /></el-icon> {{ topCard.likeCount||0 }}</span>
            </div>
          </div>
          <!-- stamp -->
          <div class="stamp stamp-l" :style="{opacity:stampL}">NOPE</div>
          <div class="stamp stamp-r" :style="{opacity:stampR}">LIKE</div>
        </div>
      </div>

      <!-- counter -->
      <div class="counter" v-if="topCard && !exiting">{{ idx+1 }} / {{ stack.length }}</div>
    </section>

    <el-empty v-if="!loading && !stack.length && !kw" description="暂无 Agent 卡片" :image-size="56" />
    <el-empty v-if="!loading && !stack.length && kw" description="无匹配卡片" :image-size="56" />
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { Search, View, Star } from '@element-plus/icons-vue'
import { cardApi } from '@/api'
import dayjs from 'dayjs'

const router=useRouter()
const loading=ref(false),cards=ref([]),kw=ref('')
const idx=ref(0),dragging=ref(false),exiting=ref(false),exitDir=ref('')
const dx=ref(0),dy=ref(0),px=ref(0),py=ref(0)
let t=null

const stack=computed(()=>cards.value)
const topCard=computed(()=>stack.value[idx.value]||null)
const bgCards=computed(()=>stack.value.slice(idx.value+1,idx.value+4))

const dragStyle=computed(()=>{
  if(!dragging.value&&!exiting.value) return{}
  const r=dx.value*0.05
  return{transform:`translate3d(${dx.value}px,${dy.value}px,0) rotate(${r}deg)`,transition:dragging.value?'none':''}
})
const stampL=computed(()=>dragging.value?Math.min(1,Math.max(0,-dx.value/80)):0)
const stampR=computed(()=>dragging.value?Math.min(1,Math.max(0,dx.value/80)):0)

onMounted(()=>{load();document.addEventListener('pointermove',onMove);document.addEventListener('pointerup',onUp)})
onUnmounted(()=>{document.removeEventListener('pointermove',onMove);document.removeEventListener('pointerup',onUp)})

const load=async()=>{
  exiting.value=false;dx.value=0;dy.value=0;loading.value=true
  try{let r;if(kw.value)r=await cardApi.search(kw.value,1,300);else r=await cardApi.getList({status:'published',pageNum:1,pageSize:300});cards.value=r.cards||[];const s=sessionStorage.getItem('ah_idx');if(s!=null){const v=parseInt(s,10);if(v>=0&&v<cards.value.length)idx.value=v;else idx.value=0}else idx.value=0}catch(e){}finally{loading.value=false}
}
const saveIdx=()=>sessionStorage.setItem('ah_idx',idx.value)

const onDown=e=>{if(exiting.value)return;dragging.value=true;px.value=e.clientX;py.value=e.clientY;dx.value=0;dy.value=0;e.target.setPointerCapture(e.pointerId)}
const onMove=e=>{if(!dragging.value)return;dx.value=e.clientX-px.value;dy.value=e.clientY-py.value}
const onUp=async()=>{if(!dragging.value)return;dragging.value=false;if(dx.value>80){await doExit('right')}else if(dx.value<-80){await doExit('left')}else{dx.value=0;dy.value=0}}

const doExit=async(dir)=>{
  exitDir.value=dir;exiting.value=true;dx.value=dir==='right'?800:-800;dy.value=0
  await new Promise(r=>setTimeout(r,380))
  if(dir==='right'&&topCard.value){saveIdx();router.push(`/card/${topCard.value.id}`);return}
  idx.value++;saveIdx();exiting.value=false;exitDir.value='';dx.value=0;dy.value=0
}
const onClick=()=>{if(dragging.value||exiting.value)return;saveIdx();router.push(`/card/${topCard.value.id}`)}
const onKw=()=>{clearTimeout(t);t=setTimeout(load,400)}
const onClear=()=>{kw.value='';sessionStorage.removeItem('ah_idx');load()}
</script>

<style lang="scss" scoped>
// hero
.hero { text-align:center; padding: 60px 0 40px; }
.hero-brand { display:inline-flex; align-items:center; gap:12px; margin-bottom:36px; }
.brand-icon { width:48px; height:48px; border-radius:14px; background: var(--c-brand); display:flex; align-items:center; justify-content:center; }
.brand-text { font-size:32px; font-weight:700; color:var(--c-text); letter-spacing:-.02em; }
.search-box { max-width:400px; margin:0 auto;
  :deep(.el-input__wrapper){ border-radius:12px; border:1px solid var(--c-border); box-shadow:none; transition:box-shadow .2s;
    &:focus-within { box-shadow: 0 0 0 3px rgba(94,92,230,.15); border-color:var(--c-brand); }
  }
}

// stack
.stack-section { max-width:400px; margin:0 auto; }
.stack { position:relative; width:100%; height:460px; }
.stack-skel { height:460px; border-radius:var(--radius); background:linear-gradient(90deg,var(--c-bg3) 25%,#eee 50%,var(--c-bg3) 75%); background-size:200% 100%; animation:sh 1.6s infinite; margin:0 12px; }
@keyframes sh { 0%{background-position:200% 0} 100%{background-position:-200% 0} }

.sc { position:absolute; inset:0 12px; border-radius:20px; border:1px solid rgba(0,0,0,.06); touch-action:none; user-select:none; overflow:hidden;
  &.bg { height:420px; top:8px; background:var(--c-bg); opacity:.45; transition: transform .4s cubic-bezier(.25,.1,.25,1); }
  &.top { height:440px; top:0; background:var(--c-bg); z-index:10; cursor:grab;
    &:not(.dragging):not(.exiting) { transition: box-shadow .3s; }
    &:hover { box-shadow: 0 20px 60px rgba(0,0,0,.1); }
  }
  &.exiting { transition: transform .38s ease, opacity .38s ease; pointer-events:none; }
  &.exitLeft { transform: translate3d(-120%,0,0) rotate(-8deg) !important; opacity:0; }
  &.exitRight { transform: translate3d(120%,0,0) rotate(8deg) !important; opacity:0; }
}
.sc-grad { position:absolute; top:0; left:0; right:0; height:6px; background: linear-gradient(90deg, var(--c-brand), #8b5cf6, var(--c-accent)); }
.sc-body { padding:34px 30px 28px; height:100%; display:flex; flex-direction:column; position:relative; z-index:1; }
.sc-tags { display:flex; gap:8px; flex-wrap:wrap; margin-bottom:20px; }
.tag { padding:4px 12px; border-radius:100px; font-size:11px; font-weight:600; background:var(--c-brand-l); color:var(--c-brand); letter-spacing:.01em; text-transform:uppercase; }
.sc-name { font-size:26px; font-weight:700; margin-bottom:14px; color:var(--c-text); line-height:1.25; letter-spacing:-.02em; }
.sc-desc { font-size:15px; color:var(--c-text2); line-height:1.65; flex:1; overflow:hidden; display:-webkit-box; -webkit-line-clamp:5; -webkit-box-orient:vertical; }
.sc-meta { display:flex; gap:24px; margin-top:20px; padding-top:16px; border-top:1px solid var(--c-border); }
.sc-stat { display:flex; align-items:center; gap:4px; font-size:13px; color:var(--c-text3); }

// stamps
.stamp { position:absolute; top:40%; z-index:2; font-size:40px; font-weight:900; letter-spacing:-.02em; padding:6px 16px; border-radius:10px; border:4px solid; pointer-events:none; }
.stamp-l { left:20px; transform:translateY(-50%) rotate(-14deg); color:var(--c-accent); border-color:var(--c-accent); }
.stamp-r { right:20px; transform:translateY(-50%) rotate(14deg); color:var(--c-brand); border-color:var(--c-brand); }

.counter { text-align:center; margin-top:20px; font-size:13px; color:var(--c-text3); font-weight:500; letter-spacing:.04em; }
</style>
