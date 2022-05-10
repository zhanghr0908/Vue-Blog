<template>
  <div>
    <el-container>
      <!--头像-->
      <el-header>
        <router-link to="/admin">
          <img alt="" class="mlogo" src="http://r9vd4j8w3.hd-bkt.clouddn.com/img/avatar.jpg">
        </router-link>
      </el-header>
      <!--主界面-->
      <el-main>
        <el-form ref="ruleForm" :model="ruleForm" :rules="rules" class="demo-ruleForm" label-width="80px" status-icon>
          <el-form-item label="用户名" prop="username">
            <!-- <el-col :span="17"> -->
              <el-input class="a" v-model="ruleForm.username" placeholder="请输入用户名" maxlength="12" type="text"></el-input>
            <!-- </el-col> -->
          </el-form-item>
          <el-form-item label="密码" prop="password">
            <!-- <el-col :span="17"> -->
              <el-input class="b" v-model="ruleForm.password" autocomplete="off" placeholder="请输入密码" type="password"></el-input>
            <!-- </el-col> -->
          </el-form-item>
          <el-form-item label="验证码" prop="verifyCode">
            <el-col :span="14">
              <el-input v-model="ruleForm.verifyCode" auto-complete="off" placeholder="请输入验证码" type="text"></el-input>
            </el-col>
            <el-col :span="10">
              <div width="100%" @click="refreshCode">
                <!--验证码组件-->
                <s-identify :identifyCode="identifyCode"></s-identify>
              </div>
            </el-col>
          </el-form-item>
          <el-form-item>
            <el-col>
              <el-button type="primary" @click="submitForm('ruleForm')">登录</el-button>
              <el-button @click="resetForm('ruleForm')">重置</el-button>
            </el-col>
          </el-form-item>
        </el-form>
      </el-main>
    </el-container>
  </div>
</template>

<script>
import SIdentify from '../components/identify'

export default {
  components: { SIdentify },
  name: 'Login',
  data() {
    return {
      ruleForm: {
        password: '',
        username: '',
        verifyCode: ''
      },
      identifyCodes: '1234567890abcdefjhijklinopqrstuvwxyz',//随机串内容
      identifyCode: '',
      rules: {
        username: [
          {required: true, message: '请输入用户名', trigger: 'blur'},
          {min: 3, max: 12, message: '长度在 3 到 12 个字符', trigger: 'blur'}
        ],
        password: [
          {required: true, message: '请输入密码', trigger: 'blur'}
        ],      
        verifyCode: [
          {required: true, message: '请输入验证码', trigger: 'blur'}
        ]
      }
    };
  },
  methods: {
    // 重置验证码
    refreshCode () {
      this.identifyCode = ''
      this.makeCode(this.identifyCodes, 5)
    },
    makeCode (o, l) {
      for (let i = 0; i < l; i++) {
        this.identifyCode += this.identifyCodes[this.randomNum(0, this.identifyCodes.length)]
      }
    },
    randomNum (min, max) {
      return Math.floor(Math.random() * (max - min) + min)
    },
    // 登录
    submitForm(formName) {
      const _this = this
      if (this.ruleForm.verifyCode.toLowerCase() !== this.identifyCode.toLowerCase()) {
        this.$message.error('请填写正确验证码')
        this.refreshCode()
        return
      }
      this.$refs[formName].validate((valid) => {
        if (valid) {
          // 提交逻辑
          this.$axios.post('/login', this.ruleForm).then((res) => {
            const token = res.headers['authorization']
            _this.$store.commit('SET_TOKEN', token) // commit会调用store中的SET_TOKEN方法
            _this.$store.commit('SET_USERINFO', res.data.data)
            _this.$router.push("/admin")
          })
        } else {
          console.log('error submit!!');
          return false;
        }
      });
    },
    //清空用户名密码
    resetForm(formName) {
      this.$refs[formName].resetFields();
    }
  },
  mounted () {
    // 初始化验证码
    this.identifyCode = ''
    this.makeCode(this.identifyCodes, 5)
  },
}
</script>

<style scoped>
.el-header, .el-footer {
  background-color: #b3c0d1;
  color: #333;
  text-align: left;
  line-height: 60px;
}
.el-main {
  color: #333;
  text-align: center;
  line-height: 160px;
}
body > .el-container {
  margin-bottom: 40px;
}
.mlogo {
  height: 70%;
  margin-top: 10px;
  margin-left: 750px;
}
.demo-ruleForm {
  max-width: 300px;
  margin: 0 auto;
}
/* /deep/.el-input {
  text-align: left;
}
/deep/.a .el-input__inner {
  width: 300px;
}
/deep/.b .el-input__inner {
  width: 300px;
} */
</style>