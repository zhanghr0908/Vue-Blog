<template>
  <el-container class="admin-container">
    <!--头部-->
    <el-header>
      <!--标题-->
      <div>
        <img alt="" height="60" src="../assets/logo.png">
        <span> 博客后台管理  </span>
        <span style="margin-left: 20px">   {{ user.username }}</span>
        <el-divider direction="vertical"></el-divider>
        <span><el-link href="/" style="color: white">主页</el-link></span>
        <Header></Header>
      </div>
      <!-- 头像 -->
      <el-dropdown v-if="user" class="user" trigger="click" @command="logout" >
        <div class="el-dropdown-link">
          <el-avatar :size="45" :src="user.avatar" fit="contain" shape="circle"></el-avatar>
        </div>
        <el-dropdown-menu slot="dropdown">
          <el-dropdown-item icon="ali-iconfont icon-logout">退出</el-dropdown-item>
        </el-dropdown-menu>
      </el-dropdown>
    </el-header>

    <!--页面主体-->
    <el-container>
      <!--侧边栏-->
      <el-aside :width="isCollapse? '64px' : '190px'">
        <div class="toggle-button" @click="isCollapse=!isCollapse"><i
            :class="isCollapse?'el-icon-s-unfold':'el-icon-s-fold'"></i></div>
        <!--菜单-->
        <el-menu :collapse="isCollapse" :collapse-transition="false" :default-active="$store.state.activePath"
                 :default-openeds="defaultOpeneds"
                 :router="true" :unique-opened="false" active-text-color="#409eff"
                 background-color="honeydew" text-color="black">
<!--          <el-menu-item index="/dashboard">-->
<!--            <i class="iconfont ali-iconfont icon-dashboard"></i>-->
<!--            <span>仪表盘</span>-->
<!--          </el-menu-item>-->
          <!-- 一级菜单 -->
          <el-submenu v-for="item in menuList" :key="item.id" :index="item.id + ''">
            <!-- 一级菜单的模板区域 -->
            <template slot="title">
              <i :class="iconsObj[item.id]" class="iconfont"></i>
              <span>{{ item.title }}</span>
            </template>
            <!-- 二级菜单 -->
            <el-menu-item v-for="subItem in item.children" :key="subItem.id" :index="subItem.path">
              <template slot="title">
                <i :class="iconsObj[subItem.id]"></i>
                <span>{{ subItem.title }}</span>
              </template>
            </el-menu-item>
          </el-submenu>
        </el-menu>
      </el-aside>
      <!--右侧内容主体-->
      <el-main :class="isCollapse?'m-el-main-width-64':'m-el-main-width-190'">
        <!--加 key 让组件被重用时 重新执行生命周期 否则在编辑文章页面路由到写文章页面时 数据被重用-->
        <router-view :key="$route.fullPath"/>
      </el-main>
    </el-container>
  </el-container>
</template>

<script>
export default {
  name: "Admin",
  data() {
    return {
      menuList: [
        {
          id: 1,
          title: '博客管理',
          children: [
            {
              id: 11,
              title: '写博客',
              path: '/writeBlog'
            },
            {
              id: 12,
              title: '文章管理',
              path: '/blogList'
            },
            {
              id: 13,
              title: '分类管理',
              path: '/type'
            },
            {
              id: 14,
              title: '标签管理',
              path: '/tags'
            },
            {
              id: 15,
              title: '评论管理',
              path: '/comments'
            },
          ]
        },
        {
          id: 2,
          title: '页面管理',
          children: [
            {
              id: 21,
              title: '友链管理',
              path: '/friendList'
            },
          ]
        },
        {
          id: 3,
          title: '系统管理',
          children: [
            {
            id: 31,
            title: '用户管理',
            path: '/userManager'
            },
          ]
        },
        {
          id: 4,
          title: '日志管理',
          children: [
            {
              id: 41,
              title: '访问日志',
              path: '/visitLog'
            }
          ]
        },
        {
          id: 5,
          title: '数据统计',
          children: [
            {
              id: 51,
              title: '访客统计',
              path: '/visitor'
            },
          ]
        },
        {
          id: 6,
          title: '初始数据',
          children: [
            {
              id: 61,
              title: '同步数据',
              path: '/initesdata'
            }
          ]
        },
      ],
      iconsObj: {
        '1': 'el-icon-menu',
        '2': 'el-icon-document-copy',
        '3': 'el-icon-s-tools',
        '4': 'el-icon-document',
        '5': 'el-icon-s-data',
        '6': 'el-icon-s-cooperation',
        '11': 'el-icon-edit',
        '12': 'el-icon-s-order',
        '13': 'el-icon-s-opportunity',
        '14': 'el-icon-s-flag',
        '15': 'el-icon-s-comment',
        '21': 'el-icon-share',
        '31': 'el-icon-user-solid',
        '41': 'el-icon-data-line',
        '51': 'el-icon-s-marketing',
        '52': 'el-icon-view',
        '61': 'el-icon-set-up',
      },
      //是否折叠
      isCollapse: false,
      //默认打开的菜单
      defaultOpeneds: ['1'],
      user: {},
    }
  },
  methods: {
    //获取缓存的用户名和头像
    getUserInfo() {
      if (this.$store.getters.getUser.username) {
        this.user.username = this.$store.getters.getUser.username
        this.user.avatar = this.$store.getters.getUser.avatar
      } else {
        this.$router.push('/login')
      }
    },
    //登出
    logout() {
      const _this = this
      this.$axios.get('/logout').then((res) => {
        _this.$store.commit('REMOVE_INFO')
        _this.$router.push('/login')
      });
    }
  },
  created() {
    this.getUserInfo()
  },
}
</script>

<style scoped>
.el-header {
  background-color: #399BDD;
  display: flex;
  justify-content: space-between;
  padding-left: 10px;
  align-items: center;
  color: #ffffff;
  font-size: 22px;
  user-select: none;
}
.el-header div {
  display: flex;
  align-items: center;
}
.el-header .title span {
  margin-left: 15px;
}
.el-aside {
  background-color: honeydew;
  position: absolute;
  top: 60px;
  bottom: 0;
  user-select: none;
}
.el-main {
  background-color: white;
  position: absolute;
  top: 60px;
  bottom: 0;
  right: 0;
  overflow-y: auto;
  overflow-x: hidden;

}
.el-aside .el-menu {
  border-right: none;
}
.m-el-main-width-190 {
  width: calc(100% - 190px);
}
.el-dropdown-menu {
  margin: 7px 0 0 0 !important;
  padding: 0 !important;
  border: 0 !important;
}
.m-el-main-width-64 {
  width: calc(100% - 64px);
}
.admin-container {
  height: 100%;
}
.toggle-button {
  background-color: honeydew;
  font-size: 20px;
  line-height: 40px;
  color: black;
  text-align: center;
  cursor: pointer;
}
.el-dropdown-link {
  outline-style: none !important;
  outline-color: unset !important;
  height: 100%;
  cursor: pointer;
}
.el-main::-webkit-scrollbar-track-piece {
  background-color: transparent;
}
.el-main::-webkit-scrollbar-track {
  -webkit-box-shadow: inset 0 0 6px transparent;
  box-shadow: inset 0 0 6px transparent;
  background-color: transparent;
}
.el-main::-webkit-scrollbar-thumb {
  -webkit-box-shadow: inset 0 0 6px #48dbfb;
  box-shadow: inset 0 0 6px #48dbfb;
  background-color: #48dbfb;
}
.el-aside {
  -ms-overflow-style: none; /* IE10 */
  scrollbar-width: none; /* Firefox */
}
.el-aside::-webkit-scrollbar {
  display: none;
}
.el-main::-webkit-scrollbar {
  width: 8px;
  height: 5px;
}
</style>