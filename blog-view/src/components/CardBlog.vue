<template>
  <div class="card">
    <p style="margin-top: 0px;">{{cardHeader}}</p>
    <el-divider></el-divider>

    <ul class="me-blog-list">
      <li class="me-blog-item" v-for="b in blogs" :key="b.id" :style="itemStyle" @click="view(b.id)">
        <a >{{b.title}}</a>
      </li>
    </ul>
  </div>
</template>

<script>
export default {
  name: 'CardBlog',
  props: {
    cardHeader: {
      type: String,
      required: true
    },
    blogs: {
      type: Array,
      required: true
    },
    itemStyle: Object
  },
  data() {
    return {}
  },
  methods: {
    view(id) {
      this.$router.push({path: `/blog/${id}`})
    }
  }
}
</script>

<style scoped>
  .card {
    width: 250px;
    height: 250px;
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

  .me-blog-list {
    list-style-type: none;
    padding-left: 0px;
  }

  .me-blog-item {
    padding: 4px;
    font-size: 14px;
    color: #5FB878;
  }

  .me-blog-item a:hover {
    text-decoration: underline;
  }

</style>