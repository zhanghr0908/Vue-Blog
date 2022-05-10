<template>
  <div>

    <!--添加-->
    <el-form inline>
      <el-form-item>
        <el-button type="primary" size="small" icon="el-icon-plus" @click="initData">初始化数据</el-button>
      </el-form-item>
    </el-form>
  </div>

</template>

<script>
export default {
  name: "InitData",
  data() {
    return {
      msg: ''
    }
  },
  methods:{
    initData(){
      const _this = this
      this.$axios.post('/initesdata').then((res) => {
        _this.msg = res.data.data
        _this.$message(_this.msg)
      });
    },
  },
}


</script>

<style scoped>
.el-button + span {
  margin-left: 10px;
}

.el-form {
  margin-top: 15px !important;
}

.el-form--inline .el-form-item {
  margin-bottom: 0;
}

</style>