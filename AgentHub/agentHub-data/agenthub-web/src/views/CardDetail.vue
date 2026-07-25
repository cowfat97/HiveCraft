<template>
  <div class="dp" v-loading="loading">
    <template v-if="card">
      <!-- back -->
      <button class="back-btn" @click="$router.push('/')">
        <el-icon><ArrowLeft /></el-icon> 返回
      </button>

      <article class="dp-card">
        <!-- header -->
        <header class="dp-hd">
          <div class="dp-tags" v-if="card.tags?.length">
            <span v-for="t in card.tags" :key="t" class="tag">{{ t }}</span>
          </div>
          <h1>{{ card.name }}</h1>
          <div class="dp-meta">
            <span>{{ fmt(card.publishedAt||card.createdAt) }}</span>
            <span>·</span>
            <span><el-icon><View /></el-icon> {{ card.viewCount||0 }}</span>
          </div>
        </header>

        <div class="dp-layout">
          <!-- content -->
          <div class="dp-main">
            <div class="markdown-body" v-if="content" v-html="renderedContent"></div>
            <el-empty v-else description="暂无内容" :image-size="48" />

            <!-- like -->
            <div class="dp-like">
              <button class="like-btn" :class="{on:liked}" @click="doLike">
                <svg width="20" height="20" viewBox="0 0 24 24" :fill="liked?'currentColor':'none'" stroke="currentColor" stroke-width="2"><path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z"/></svg>
                {{ likeN }}
              </button>
            </div>

            <!-- comments -->
            <section class="cmt">
              <h2>评论 <span class="cmt-n">{{ cmtN }}</span></h2>

              <div class="cmt-form">
                <el-input v-model="cmtTxt" type="textarea" :rows="3" placeholder="写下你的想法…" maxlength="2000" show-word-limit />
                <el-button type="primary" :disabled="!cmtTxt.trim()" @click="doCmt" round>发表</el-button>
              </div>

              <div v-if="cmts.length" class="cmt-list">
                <div v-for="c in cmts" :key="c.id" class="comment-item">
                  <div class="comment-header">
                    <span class="commenter-name">{{ c.commenterName||'匿名' }}</span>
                    <span class="comment-time">{{ fmt(c.createdAt) }}</span>
                  </div>
                  <div class="comment-content">{{ c.content }}</div>
                  <div class="cmt-actions">
                    <el-button link size="small" @click="replyTo=c;replyTxt=''">回复</el-button>
                    <el-button link size="small" @click="doLikeCmt(c)"><el-icon><Star /></el-icon> {{ c.likeCount||0 }}</el-button>
                  </div>

                  <div v-if="replyTo?.id===c.id" class="reply-box">
                    <el-input v-model="replyTxt" type="textarea" :rows="2" :placeholder="'回复 '+c.commenterName" />
                    <div class="reply-box-ft">
                      <el-button type="primary" size="small" @click="doReply(c)">发送</el-button>
                      <el-button size="small" @click="replyTo=null">取消</el-button>
                    </div>
                  </div>

                  <div v-if="c.replies?.length" class="replies">
                    <div v-for="r in c.replies" :key="r.id" class="reply-item">
                      <div class="reply-header">
                        <span class="commenter-name">{{ r.commenterName||'匿名' }}</span>
                        <span class="reply-to">回复</span>
                        <span class="reply-target">@{{ r.replyToName }}</span>
                        <span class="comment-time">{{ fmt(r.createdAt) }}</span>
                      </div>
                      <div class="comment-content">{{ r.content }}</div>
                    </div>
                  </div>
                </div>
              </div>
              <el-empty v-else description="暂无评论" :image-size="44" />
            </section>
          </div>

          <!-- sidebar -->
          <aside class="dp-side">
            <div class="side-card">
              <h3>Agent 信息</h3>
              <dl>
                <dt>状态</dt><dd><span class="badge" :class="card.status">{{ card.status }}</span></dd>
                <dt>Card ID</dt><dd><code>{{ card.id }}</code></dd>
                <dt>Agent ID</dt><dd><code>{{ card.agentId }}</code></dd>
              </dl>
            </div>

            <div class="side-card" v-if="card.skills?.length">
              <h3>技能</h3>
              <div v-for="s in card.skills" :key="s.id" class="sk-item">
                <div class="sk-name">{{ s.name }}</div>
                <div class="sk-desc" v-if="s.description">{{ s.description }}</div>
              </div>
            </div>
          </aside>
        </div>
      </article>
    </template>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { View, Star, ArrowLeft } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { marked } from 'marked'
import { cardApi, commentApi, likeApi } from '@/api'
import dayjs from 'dayjs'

const route=useRoute(),router=useRouter()
const UID=1,UT='user'
const loading=ref(true),card=ref(null),content=ref(''),cmts=ref([]),cmtN=ref(0),cmtTxt=ref('')
const replyTo=ref(null),replyTxt=ref(''),liked=ref(false),likeN=ref(0),cmtLiked=ref({})
const renderedContent=computed(()=>content.value?marked(content.value):'')

onMounted(async()=>{
  const id=route.params.id;loading.value=true
  try{const[cd,ct]=await Promise.all([cardApi.getDetail(id),cardApi.getContent(id)]);card.value=cd;content.value=ct;const[cn,lk]=await Promise.all([likeApi.getCount(id,'agent_card'),likeApi.check(UID,UT,id,'agent_card')]);likeN.value=cn;liked.value=lk;await ldCmt()}catch(e){router.push('/')}finally{loading.value=false}
})
const ldCmt=async()=>{try{const r=await commentApi.getCardComments(card.value.id);cmts.value=r.comments||[];cmtN.value=r.total||0}catch(e){}}
const doLike=async()=>{try{if(liked.value){await likeApi.unlike(UID,UT,card.value.id,'agent_card');liked.value=false;likeN.value=Math.max(0,likeN.value-1)}else{await likeApi.like({userId:UID,userName:'匿名',userType:UT,targetId:card.value.id,targetType:'agent_card'});liked.value=true;likeN.value++}}catch(e){}}
const doCmt=async()=>{try{await commentApi.create({cardId:card.value.id,commenterId:UID,commenterName:'匿名',commenterType:UT,content:cmtTxt.value});ElMessage.success('已提交');cmtTxt.value='';ldCmt()}catch(e){ElMessage.error('失败')}}
const doReply=async(p)=>{try{await commentApi.create({cardId:card.value.id,commenterId:UID,commenterName:'匿名',commenterType:UT,content:replyTxt.value,parentId:p.id,replyToId:p.id,replyToName:p.commenterName});ElMessage.success('已回复');replyTo.value=null;ldCmt()}catch(e){ElMessage.error('失败')}}
const doLikeCmt=async(c)=>{try{if(cmtLiked.value[c.id]){await likeApi.unlike(UID,UT,c.id,'comment');c.likeCount=Math.max(0,(c.likeCount||0)-1);cmtLiked.value[c.id]=false}else{await likeApi.like({userId:UID,userName:'匿名',userType:UT,targetId:c.id,targetType:'comment'});c.likeCount=(c.likeCount||0)+1;cmtLiked.value[c.id]=true}}catch(e){}}
const fmt=d=>dayjs(d).format('YYYY-MM-DD HH:mm')
</script>

<style lang="scss" scoped>
.dp { max-width:960px; margin:0 auto; }
.back-btn { display:inline-flex; align-items:center; gap:6px; background:none; border:none; font-size:14px; color:var(--c-brand); cursor:pointer; padding:0; margin-bottom:20px; font-weight:500;
  &:hover { opacity:.7; }
}

.dp-card { background:var(--c-bg); border-radius:24px; border:1px solid rgba(0,0,0,.06); overflow:hidden; }

// header
.dp-hd { padding:40px 44px 32px; border-bottom:1px solid var(--c-border);
  h1 { font-size:36px; font-weight:700; letter-spacing:-.03em; margin: 16px 0 14px; }
}
.dp-tags { display:flex; gap:8px; flex-wrap:wrap; }
.tag { padding:3px 12px; border-radius:100px; font-size:11px; font-weight:600; background:var(--c-brand-l); color:var(--c-brand); letter-spacing:.01em; text-transform:uppercase; }
.dp-meta { display:flex; align-items:center; gap:12px; font-size:14px; color:var(--c-text3); }

// layout
.dp-layout { display:flex; gap:0; }
.dp-main { flex:1; min-width:0; padding:40px 44px 48px;
  border-right:1px solid var(--c-border);
}

// sidebar
.dp-side { width:256px; flex-shrink:0; padding:32px 24px; display:flex; flex-direction:column; gap:20px;
  position:sticky; top:24px; align-self:flex-start;
}
.side-card {
  h3 { font-size:11px; font-weight:700; color:var(--c-text3); text-transform:uppercase; letter-spacing:.06em; margin-bottom:14px; }
  dl { display:grid; grid-template-columns: auto 1fr; gap:8px 16px; font-size:13px; }
  dt { color:var(--c-text3); font-weight:400; }
  dd { color:var(--c-text2); text-align:right; code { font-family:"SF Mono",Consolas,monospace; font-size:11px; color:var(--c-text3); } }
}
.badge { display:inline-block; padding:2px 10px; border-radius:100px; font-size:11px; font-weight:600;
  &.PUBLISHED { background:#d1fae5; color:#065f46; }
  &.DRAFT { background:var(--c-bg3); color:var(--c-text2); }
  &.ARCHIVED { background:#fef3c7; color:#92400e; }
}
.sk-item { padding:8px 0; &:not(:last-child){ border-bottom:1px solid var(--c-border); } }
.sk-name { font-size:13px; font-weight:600; margin-bottom:2px; }
.sk-desc { font-size:12px; color:var(--c-text3); line-height:1.5; }

// like
.dp-like { text-align:center; padding:32px 0; margin-top:24px; border-top:1px solid var(--c-border); }
.like-btn { display:inline-flex; align-items:center; gap:8px; padding:12px 28px; border-radius:100px; border:1px solid var(--c-border); background:var(--c-bg); font-size:15px; font-weight:600; color:var(--c-text2); cursor:pointer; transition: all .2s;
  &:hover { border-color:var(--c-brand); }
  &.on { background:var(--c-brand-l); border-color:var(--c-brand); color:var(--c-brand); }
}

// comments
.cmt { margin-top:36px; padding-top:32px; border-top:1px solid var(--c-border);
  h2 { font-size:20px; font-weight:600; margin-bottom:20px; }
}
.cmt-n { color:var(--c-text3); font-weight:400; font-size:16px; margin-left:4px; }
.cmt-form { margin-bottom:32px; .el-button { margin-top:12px; } }
.cmt-list { min-height:40px; }
.cmt-actions { display:flex; gap:14px; margin-top:6px; }

// reply
.reply-box { margin-top:14px; padding:16px; background:var(--c-bg2); border-radius:12px; }
.reply-box-ft { display:flex; gap:8px; margin-top:10px; }

@media(max-width:780px){
  .dp-layout { flex-direction:column; }
  .dp-main { border-right:none; padding:32px 24px; }
  .dp-side { width:100%; position:static; flex-direction:row; flex-wrap:wrap; .side-card{flex:1;min-width:160px;} }
  .dp-hd { padding:28px 24px; h1 { font-size:28px; } }
}
</style>
