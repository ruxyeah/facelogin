$(function(){
	let mediaStreamTrack=null;
	openMedia();
	setTimeout("tishi()","1000");
	setTimeout("tishi2()","3000");
	setTimeout("takePhoto()","5000");

})
var number=0;
var maxRetry=3;  // 默认最大重试次数，口罩模式下会增加到5次
function tishi(){
	$("#flag").html("正在打开摄像头")
}
function tishi2(){
	$("#flag").html("请正视摄像头（支持口罩识别）")
}
function tishi3(){
    window.location.href="/";
}
    function openMedia() {
        let constraints = {
            video: { width: 500, height: 500 },
            audio: false
        };

        // 检查浏览器是否支持 getUserMedia
        if (!navigator.mediaDevices || !navigator.mediaDevices.getUserMedia) {
            $("#flag").html("浏览器不支持摄像头访问，请使用 Chrome/Firefox 并通过 HTTPS 访问");
            console.error("getUserMedia 不可用，请确保：1. 使用现代浏览器 2. 通过 HTTPS 或 localhost 访问");
            return;
        }

        //获得video摄像头
        let video = document.getElementById('video');
        navigator.mediaDevices.getUserMedia(constraints)
            .then((mediaStream) => {
                // 修复：使用索引 0 获取视频轨道（因为 audio: false，只有视频轨道）
                mediaStreamTrack = typeof mediaStream.stop === 'function' ? mediaStream : mediaStream.getTracks()[0];
                video.srcObject = mediaStream;
                video.play();
            })
            .catch((error) => {
                console.error("摄像头访问失败:", error);
                let errorMsg = "摄像头打开失败：";
                switch(error.name) {
                    case 'NotAllowedError':
                        errorMsg += "请允许摄像头权限";
                        break;
                    case 'NotFoundError':
                        errorMsg += "未检测到摄像头设备";
                        break;
                    case 'NotReadableError':
                        errorMsg += "摄像头被其他程序占用";
                        break;
                    case 'OverconstrainedError':
                        errorMsg += "摄像头不支持指定分辨率";
                        break;
                    case 'SecurityError':
                        errorMsg += "请通过 HTTPS 或 localhost 访问";
                        break;
                    default:
                        errorMsg += error.message || "未知错误";
                }
                $("#flag").html(errorMsg);
            });
    }

    // 拍照
    function takePhoto() {
        //获得Canvas对象
        number++;
        let video = document.getElementById('video');
        let canvas = document.getElementById('canvas');
        let ctx = canvas.getContext('2d');
        ctx.drawImage(video, 0, 0, 500, 500);
		// toDataURL  ---  可传入'image/png'---默认, 'image/jpeg'
        let img = document.getElementById('canvas').toDataURL();
        // 这里的img就是得到的图片
        console.log('img-----', img);
        document.getElementById('imgTag').src=img;
		$("#flag").html("正在识别");
        $.ajax({
            url:"/login/searchface",    //请求的url地址
            dataType:"json",   //返回格式为json
            async:true,//请求是否异步，默认为异步，这也是ajax重要特性
            // contentType:"application/json",
            data: {"imagebast64": img} , //参数值
            type: "POST", //请求方式
            success: function (data) {
                // 处理失败情况
                if(data == "fail") {
                    handleRecognitionFailure("未检测到人脸");
                    return;
                }

                // 解析分数和口罩状态
                let score = parseFloat(data.score);
                let hasMask = data.has_mask === true;

                // 根据口罩状态调整阈值：口罩模式70分，正常模式80分
                let threshold = hasMask ? 70 : 80;

                // 口罩模式下增加重试次数
                if(hasMask) {
                    maxRetry = 5;
                }

                console.log("识别分数:", score, "阈值:", threshold, "口罩模式:", hasMask);

                if(score < threshold) {
                    // 识别失败
                    let msg = hasMask
                        ? "口罩识别中，当前分数:" + score.toFixed(1) + " (需>" + threshold + ")"
                        : "识别失败，当前分数:" + score.toFixed(1) + " (需>" + threshold + ")";
                    handleRecognitionFailure(msg);
                } else {
                    // 识别成功
                    let successMsg = hasMask ? "口罩人脸识别成功！" : "人脸识别成功！";
                    $("#flag").html(successMsg + " 分数:" + score.toFixed(1));
                    setTimeout(function(){
                        window.location.href="/login/facesucceed";
                    }, 500);
                }
            },
            error: function(xhr, status, error) {
                console.error("请求失败:", error);
                handleRecognitionFailure("网络请求失败，请重试");
            }
        })
    }

    // 处理识别失败的通用函数
    function handleRecognitionFailure(message) {
        if(number < maxRetry) {
            $("#flag").html(message + " - 3秒后重试(" + number + "/" + maxRetry + ")");
            setTimeout("takePhoto()", 3000);
        } else {
            $("#flag").html("识别失败，请使用账号密码登录 - 3秒后返回主页");
            setTimeout("tishi3()", 3000);
        }
    }

    // 关闭摄像头
    function closeMedia() {
        mediaStreamTrack.stop();
    }