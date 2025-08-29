<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Loginpage</title>
<link
	href="https://fonts.googleapis.com/css?family=Karla:400,700&display=swap"
	rel="stylesheet">
<link rel="stylesheet"
	href="https://cdn.materialdesignicons.com/4.8.95/css/materialdesignicons.min.css">
<link rel="stylesheet"
	href="https://stackpath.bootstrapcdn.com/bootstrap/4.4.1/css/bootstrap.min.css">
</head>
<body>
	<div class="container-fluid">
		<div class="row">
			<div class="col-sm-4 login-section-wrapper">
				</div>
				<div class="login-wrapper my-auto">
					<div>
						<button id="loginButton" style="border: 0; background: none;"
							onclick="signIn()" disabled>
							<h1 class="login-title" id="loginFont" style="color: #000;">SIGN
								IN</h1>
						</button>
						<button id="signupButton" style="border: 0; background: none;"
							onclick="signUp()">
							<h1 class="login-title" id="signupFont" style="color: #ced4da;">SIGN UP</h1>
						</button>
					</div>
					<div id="loginPage">
						<form action="/user/loginProc.do" method="post">
							<div class="form-group">
								<label for="email">아이디</label> <input name="id" id="id"
									class="form-control" placeholder="enter your ID">
							</div>
							<div class="form-group mb-4">
								<label for="password">비밀번호</label> <input type="password"
									name="pwd" id="pwd" class="form-control"
									placeholder="enter your passsword">
							</div>
							<input name="login" class="btn btn-block login-btn" type="submit"
								value="Login">
						</form>
					</div>

					<div id="signupPage" style="display: none">
						<div class="form-group">
							<label for="name">이름</label> <input name="signupName"
								id="signupName" class="form-control" placeholder="your name">
						</div>
						<div class="form-group">
							<label for="email">아이디</label> <input name="signupId"
								id="signupId" class="form-control" placeholder="enter your ID">
						</div>
						<div class="form-group mb-4">
							<label for="password">비밀번호</label> <input type="password"
								name="signupPwd" id="signupPwd" class="form-control"
								placeholder="enter your passsword">
						</div>
						<div class="form-group mb-4">
							<label for="password">비밀번호 재확인</label> <input type="password"
								name="checkPwd" id="checkPwd" class="form-control"
								placeholder="check your passsword">
						</div>
						<input name="login" class="btn btn-block login-btn" type="button"
							value="SignUp" onclick="signupProc()">
					</div>
				</div>
			</div>
			<div class="col-sm-8 px-0 d-none d-sm-block">
				<img src="/assets/images/healingMain.jpg" alt="login image"
					class="login-img">
			</div>
		</div>
	</div>
	<script src="https://code.jquery.com/jquery-3.4.1.min.js"></script>
	<script
		src="https://cdn.jsdelivr.net/npm/popper.js@1.16.0/dist/umd/popper.min.js"></script>
	<script
		src="https://stackpath.bootstrapcdn.com/bootstrap/4.4.1/js/bootstrap.min.js"></script>
	<script src="https://unpkg.com/sweetalert/dist/sweetalert.min.js"></script>
	<script>
		function signIn() {
			document.getElementById('loginPage').style = "";
			document.getElementById('signupPage').style = "display:none";
			document.getElementById('loginFont').style = "color:#000";
			document.getElementById('signupFont').style = "color:#ced4da";
			let button_a = document.getElementById('loginButton');
			let button_b = document.getElementById('signupButton');

			button_a.disabled = true;
			button_b.disabled = false;
		}
		function signUp() {
			document.getElementById('loginPage').style = "display:none";
			document.getElementById('signupPage').style = ""
			document.getElementById('loginFont').style = "color:#ced4da";
			document.getElementById('signupFont').style = "color:#000";
			let button_a = document.getElementById('loginButton');
			let button_b = document.getElementById('signupButton');

			button_a.disabled = false;
			button_b.disabled = true;
		}
	</script>
	<script>
	function signupProc() { 
		let user_id = document.getElementById('signupId').value;
		let user_pwd = document.getElementById('signupPwd').value;
		let user_checkPwd = document.getElementById('checkPwd').value;
		let user_name = document.getElementById('signupName').value;
		
		console.log(user_pwd);
		console.log(user_checkPwd);
		
		if(user_name.trim() == '') {
			alert('이름을 입력해주세요');
			return false;
		}else if(user_pwd.trim() == '') {
			alert('비밀번호를 입력해주세요');
			return false;
		}else if(user_id.trim() == '') {
			alert('아이디를 입력해주세요');
			return false;
		}
		
		if(user_pwd != user_checkPwd) {
			alert('비밀번호가 일치하지 않습니다.');
			return false;
		}
		
		$.ajax({
			url: "/user/signupProc.do",
			type: "POST",
			data: {"user_id":user_id,"user_pwd":user_pwd,"user_name":user_name}, 
			success:function(result) {
				doSwal('관리자 승인 후 완료됩니다!', '', 'success');
				setTimeout("location.reload()", 3000);
			},
			error:function(e) {
				alert('이미 존재하는 아이디입니다.');
			}
		});
	}
	</script>
</body>
</html>