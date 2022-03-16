<template>
  <div id="group">
    <!-- 搜索框 -->
    <el-header class="chat-friendsList-header">
      <el-row>
        <el-col :span="16">
          <el-input placeholder="搜索" clearable prefix-icon="el-icon-search" size="mini"
                    style="width: 170px">
          </el-input>
        </el-col>
        <el-col :span="4">
          <el-button size="mini">搜索群</el-button>
        </el-col>

      </el-row>
    </el-header>

    <!-- 群列表 -->
    <el-main class="chat-groupList-main scrollbar">

      <el-collapse>
        <el-collapse-item class="groupItem">
          <template slot="title">
            <i class="el-icon-cloudy"></i> <span>群1</span>
          </template>
          <li>
            <el-col :span="12">祈雪灵祝</el-col>
            <el-col :span="12" style="text-align: right; padding-right: 10px">
              <el-tag size="small">群主</el-tag>
            </el-col>
          </li>
          <li>青蛙小新</li>
        </el-collapse-item>

        <el-collapse-item class="groupItem">
          <template slot="title">
            <i class="el-icon-cloudy"></i><span>群2</span>
          </template>
          <li>
            <el-col :span="12">一杯香梨</el-col>
            <el-col :span="12" style="text-align: right; padding-right: 10px">
              <el-tag size="small">群主</el-tag>
            </el-col>
          </li>
          <li>祈雪灵祝</li>
        </el-collapse-item>

        <el-collapse-item class="groupItem">
          <template slot="title">
            <i class="el-icon-cloudy"></i><span>群3</span>
          </template>
          <li>
            <el-col :span="12">一杯香梨</el-col>
            <el-col :span="12" style="text-align: right; padding-right: 10px">
              <el-tag size="small">群主</el-tag>
            </el-col>
          </li>
          <li>祈雪灵祝</li>
        </el-collapse-item>

        <el-divider><i class="el-icon-sunrise"></i></el-divider>
      </el-collapse>

    </el-main>
  </div>
</template>

<script>
export default {
  name: "Group"
}
</script>

<style>
/* 搜索 */
.chat-friendsList-header {
  height: 60px;
  line-height: 60px;
  border-right: 1px rgba(89, 89, 89, 0.3) solid;
  padding-right: 5px;
  border-radius: 3px;
}

/* 群列表 */
.chat-groupList-main {
  width: 100%;
  height: 478px;
  border-top: 1px rgba(89, 89, 89, 0.3) solid;
  border-right: 1px rgba(89, 89, 89, 0.3) solid;
  border-radius: 3px;
}

.el-collapse {
  border: 0 !important;
}

/*滚动条整体样式*/
.scrollbar::-webkit-scrollbar {
  width: 5px;
  height: 0;
}

.el-collapse-item__wrap {
  border: 0 !important;
  background: none !important;
}

.el-collapse-item__header {
  background: none !important;
  border: 0 !important;
}

.el-collapse-item__content {
  padding: 0 !important;
}

.el-collapse-item__content li {
  list-style: none;
  height: 36px;
  line-height: 36px;
  margin-bottom: 5px;
  margin-top: 5px;
  padding-left: 22px;
  border-radius: 3px;
}
</style>