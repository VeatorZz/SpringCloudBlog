// 退出登录
function logout() {
    if (confirm("是否确定退出？")) {
        window.localStorage.removeItem(token_key);
        window.localStorage.removeItem("uid");
        window.localStorage.removeItem("manager");
        window.localStorage.removeItem("username");
        location.href = "index.html";
    }
}