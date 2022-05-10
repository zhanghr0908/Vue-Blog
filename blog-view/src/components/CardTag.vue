<template>
  <div class="card">
    <p style="margin-top: 0px;">最热标签</p>
    <el-divider></el-divider>

    <ul class="me-tag-list">
      <li class="me-tag-item" v-for="t in tags" :key="t.id">
        <el-button size="mini" type="primary" round plain>{{t.tagName}}</el-button>
      </li>
    </ul>
  </div>
</template>

<script>
export default {
  name: 'CardTag',
  props: {
    tags: Array
  },
  data() {
    return {}
  },
  methods: {
  }
}
</script>

<style scoped>
  .card {
    width: 250px;
    height: 200px;
    margin-top: 20px;
    padding: 20px 0px 0px 0px;
    background-color: white;
    text-align: center;
  }

  .card:hover {
    -webkit-box-shadow: #ccc 0px 10px 10px;
    -moz-box-shadow: #ccc 0px 10px 10px;
    box-shadow: #ccc 0px 10px 10px;
  }

  .me-tag-more {
    font-size: 16px;
    color: #78b6f7;
  }

  .me-tag-list {
    list-style-type: none;
    padding-left: 0px;
  }

  .me-tag-item {
    display: inline-block;
    padding: 4px;
    font-size: 14px;
    color: #5FB878;
  }

  .me-tag-item a:hover {
    text-decoration: underline;
  }

</style>