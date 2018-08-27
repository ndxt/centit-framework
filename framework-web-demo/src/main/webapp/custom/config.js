define(function(require) {
    return {

    	/**
    	 *  用户链接配置信息
    	 */
        URL: {
// 			// 首页
//    		Dashboard: window.ContextPath + 'modules/sys/schedule/schedule.jsp'
//
//			// 数据字典，{{code}}代表数据字典编码
//			Dictionary: 'data/dictionary/{{code}}.json',
//
//			// 用户信息
//			UserInfo: 'data/user-info.json',
//
//			// 用户个性化设置
//			UserSetting: 'data/user-setting.json',
//
//			// 首页菜单
//			Menu: 'data/menu.json',
//
//			// 注销WWWW
//			Logout: 'login.html',
        },

        /**
         * 主题相关配置
         */
        Theme: {
//            // 默认主题
//            DefaultTheme: 'xkj',
//
//             // 默认颜色
//             DefaultColor: 'sky_blue',
//
//             // 默认菜单布局
//             DefaultLayout: 'left',
//
//             // 首页加载动画效果主题
//             LoadAnimation: 'CentiUI',
//
//             // 自定义 css 文件路径
//             CustomCSS: ['custom/style.css'],
//
//             // 图标 css 文件路径
//             IconCSS: ['ui/css/icon-black-16.css'],
//
//             // 模板路径
//             Template: null
        },

        /**
         * 菜单相关配置项
         */
        Menu: {
//        	// 菜单数据加载器，可以处理传入的菜单数据
//            Loader: null,
//
//            // 菜单图标，在后台数据无icon字段时手动设定
//            Icons: null,
//
//            // 菜单大图标，在使用混合型菜单时设定
//            LargeIcons: null,
//
            // 首页配置
            Dashboard: {
            	// 首页ID
              id: 'Dashboard',

            	// 首页标特
              text: '我的首页',

            	// 首页图标
              icon: 'icon-base icon-base-home',

            	// 首页链接
//              url: URL.Dashboard,

            	// 打开方式
//              external: false,

            	// 是否可以被关闭
              closable: false
            }
        },

        /**
         * 数据字典配置项
         */
        Dictionary: {
             // 缓存数据字段，数组保存需要缓存的数据字典Code，更多信息参考：loader.dictionary.js
             //Init: ['OptType', 'OptLevel']
        },

        /**
         * 初始化缓存
         */
        Cache: {
            // 缓存初始加载，更多信息参考：loader.system.js
//          Init: [
//              // 在原封不动保存所得数据的基础上，如果数据是Array，复制一份Map的备份，key为对象指定的字段的值，value为对象指定字段的值
//              {id: 'UnitInfo', url: 'data/unit-info.json', key: 'unitCode', value: 'unitName'},
//
//              // 在原封不动保存所得数据的基础上，如果数据是Array，复制一份Map的备份，key为对象指定的字段的值，value为对象
//              {id: 'OptInfo', url: 'data/opt-info.json', key: 'optCode'},
//
//              // 原封不动保存所得数据
//              {id: 'UserInfo', url: 'data/user-info.json'}
//          ]
        },

        /**
         * 系统相关配置
         */
        System: {
//     		// 系统title
        	Title: '南大先腾',

    		// 系统头部title
    		HeaderTitle: '我的系统标题',

    		// 系统尾部title
    		FooterTitle: '项目的技术服务信息',
//
//            // 首页加载时缓冲动画效果
//            LoadingAnimation: true,
//
//            // 处理Ajax数据
//            AjaxLoader: null,
//
//            // 处理Ajax错误数据
//            AjaxErrorLoader: null,
//
//            // debug模式，打印日志
//            Debug: true,
//
//            // 使用easyui布局
//            EasyUI: true
        }
    };
});
