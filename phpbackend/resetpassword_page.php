<!DOCTYPE html>
<html lang="en">
<?php
    session_start();
    if (isset($_SESSION['token']))
    {
        $token = $_SESSION['token'];
    }else
    {
        header('location: errorPAGE.php');
    }
?>
<head>

    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="description" content="">
    <meta name="author" content="">

    <title>Reset Password</title>

    <script src="../js/jquery-1.11.1.min.js"></script>
    <!-- Bootstrap Core CSS -->
    <link href="../bower_components/bootstrap/dist/css/bootstrap.min.css" rel="stylesheet">

    
    <!-- Custom CSS -->
    <link href="../dist/css/sb-admin-2.css" rel="stylesheet">

   

    <!-- Custom Fonts -->
    <link href="../bower_components/font-awesome/css/font-awesome.min.css" rel="stylesheet" type="text/css">

    

</head>

<body style = "background: #00253A">
    <input type = "hidden" value = "<?php echo $token; ?>" id = "token">
    <div class = "container" style = "background: #00253A">
        <div class = "row">
            <center>
                <img src = "../images/logofin.png" style = "padding-top:10px" height = "25%" width = "25%"> </img>
            </center>
        </div>
        <div class ="row" style = "padding-top: 15px">
            <div class = "col-md-3">

            </div>

            <div class = "well col-md-6" style = "background:#fff">
                <div class = "form-group">
                    <p style = "font-size:25px;color:black">&nbsp;
                        <img src = "../images/logolil.png" height = "6%" width = "6%"> </img>
                        <font face="Berlin Sans FB" size = "6px" color = "grey">&nbsp;RESET PASSWORD</font>
                    </p>
                    <hr>
                        <div class ="row">
                            <div class = "col-md-1">
                            </div>
                            <div class = "col-md-10">
                                <div class="alert alert-danger hide" role="alert" id = "divalert">
                                    <div class = "hide" id = "password_div">
                                        <span class = "glyphicon glyphicon-exclamation-sign" style = "padding-left:30px"></span>&nbsp;
                                        <span id = "password_message"></span>
                                    </div>
                                    <div class = "hide" id = "confirmpassword_div">
                                        <span class = "glyphicon glyphicon-exclamation-sign" style = "padding-left:30px"></span>&nbsp;
                                        <span id = "confirmpassword_message"></span>
                                    </div>
                                </div>
                            </div>
                            <div class = "col-md-1">
                            </div>
                        </div>
                        <div class ="row">
                            <div class = "col-md-4">
                                    <label style = "display:block;text-align:right;color:grey;font-size:16px">New password:</label>
                            </div>
                            <div class = "col-md-6">
                                <input type ="password" class = "form-control" id = "password" onblur = "passwordblur()">                    
                            </div>
                            <div class = "col-md-1">
                                 <div class="tooltip-demo">
                                    <span class = "fa fa-times-circle-o hide" style = "font-size:28px; color:#E85858" id = "password_sign_times" data-toggle="tooltip" data-placement="top" title="Please don't leave this field empty."> </span>
                                    <span class = "fa fa-times-circle-o hide" style = "font-size:28px; color:#E85858" id = "password_sign_times2" data-toggle="tooltip" data-placement="top" title="Your password is weak. Please try another one."> </span>
                                    <span class = "fa fa-check-circle-o hide" style = "font-size:28px; color:#66FF66" id = "password_sign_check" data-toggle="tooltip" data-placement="top" title="You're passwords matched."> </span>
                                </div>
                            </div>
                            <div class = "col-md-1">
                            </div>
                        </div>
                        <div class = "row" style = "padding-top:20px">
                            <div class = "col-md-6" style = "text-align:right">
                                <i style = "font-size:14px" class = "hide" id = "strtxt">Password Strength:</i>
                            </div>
                            <div class = "col-md-4">
                                <div class="progress hide" id = "strbar">
                                    <div class="progress-bar hide" id = "progressbar" role="progressbar" aria-valuenow="40" aria-valuemin="0" aria-valuemax="100" style="width:100%">
                                        <span id = "verdict"></span>
                                    </div>
                                </div>
                            </div>
                            <div class = "col-md-2">
                            </div>
                        </div>

                        <div class ="row">
                            <div class = "col-md-4">
                                <label style = "display:block;text-align:right;color:grey;font-size:16px">Confirm password:</label>
                            </div>
                            <div class = "col-md-6">
                                <input type ="password" class = "form-control" id = "confirmpassword" onblur = "confirmpasswordblur()">
                            </div>
                            
                            <div class = "col-md-1">
                                <div class="tooltip-demo">
                                    <span class = "fa fa-times-circle-o hide" style = "font-size:28px; color:#E85858" id = "confirmpassword_sign_times" data-toggle="tooltip" data-placement="top" title="Please don't leave this field empty."> </span>
                                    <span class = "fa fa-times-circle-o hide" style = "font-size:28px; color:#E85858" id = "confirmpassword_sign_times2" data-toggle="tooltip" data-placement="top" title="The confirm password you've entered doesn't match."> </span>
                                    <span class = "fa fa-check-circle-o hide" style = "font-size:28px; color:#66FF66" id = "confirmpassword_sign_check" data-toggle="tooltip" data-placement="top" title="You're passwords matched."> </span>
                                    <span class = "fa fa-times-circle-o hide" style = "font-size:28px; color:#E85858" id = "confirmpassword_sign_times3" data-toggle="tooltip" data-placement="top" title=""> </span>
                                </div>
                            </div>
                            <div class = "col-md-1">
                            </div>
                        </div>

                        <br>
                        <div class = "row">
                            <center> <button class = "btn btn-info btn-lg" id = "submit" onclick = "iclick()" title = "Click here to create your new Nucleus account.">
                             &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;SAVE CHANGES&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                             </button> </center>
                            <br>
                        </div>
                </div>
            </div>
            
            <div class = "col-md-3">
            </div>
        </div>
        <br><br><br>
    </div>
    <input type = "hidden" id = "password_hid">
    <input type = "hidden" id = "confirmpassword_hid">
    <script>

        $(document).ready(function () {

            $("#submit").click(function(){
                $(this).text("Saving changes, Please wait..");
                $(this).css("background-color", "#31708f");
                $(this).css("width","55%");
                $(".form-group :input").prop("readonly", true);
                setTimeout(function(){
                    var divalert = document.getElementById("divalert");
                    var password = document.getElementById("password");
                    var confirmpassword = document.getElementById("confirmpassword");
                    var email = document.getElementById("email");
                    var password_hid = document.getElementById("password_hid");
                    var confirmpassword_hid = document.getElementById("confirmpassword_hid");
                    var ctr = 0;


                    var mypassword = $("#password").val();
                    var mytoken = $("#token").val();

                    alert(mypassword);
                    alert(mytoken);

                    var password_sign_times = document.getElementById("password_sign_times");
                    var password_sign_times2 = document.getElementById("password_sign_times2");
                    var password_sign_check = document.getElementById("password_sign_check");

                    var password_sign_times = document.getElementById("password_sign_times");
                    var password_sign_times2 = document.getElementById("password_sign_times2");
                    var password_sign_check = document.getElementById("password_sign_check");

                    var confirmpassword_sign_times = document.getElementById("confirmpassword_sign_times");
                    var confirmpassword_sign_times2 = document.getElementById("confirmpassword_sign_times2");
                    var confirmpassword_sign_times3 = document.getElementById("confirmpassword_sign_times3");
                    var confirmpassword_sign_check = document.getElementById("confirmpassword_sign_check");

                    var verdict = document.getElementById("verdict");
                    var verdictvalue = $("#verdict").text();

                    if (password.value != "" && verdictvalue != "Weak")
                    {  
                        password_sign_check.className = "fa fa-check-circle-o show";
                        password_sign_times.className = "fa fa-times-circle-o hide";
                        password_sign_times2.className = "fa fa-times-circle-o hide";
                        password.style.borderColor = "#66FF66";
                        password_hid.value = "";
                        password_div.className = "hide";
                    }
                    else if (verdictvalue == "Weak")
                    {
                        password_sign_check.className = "fa fa-check-circle-o hide";
                        password_sign_times.className = "fa fa-times-circle-o hide";
                        password_sign_times2.className = "fa fa-times-circle-o show";
                        password.style.borderColor = "#E85858";
                        password_hid.value = "Your password is weak. Please try another one.";
                    }
                    else
                    {
                        password_sign_check.className = "fa fa-check-circle-o hide";
                        password_sign_times.className = "fa fa-times-circle-o show";
                        password_sign_times2.className = "fa fa-times-circle-o hide";
                        password.style.borderColor = "#E85858";
                        password_hid.value = "Please enter your password.";
                    }

                    if (confirmpassword.value != "" && confirmpassword.value == password.value && verdictvalue != "Weak")
                    {  
                        confirmpassword_sign_check.className = "fa fa-check-circle-o show";
                        confirmpassword_sign_times.className = "fa fa-times-circle-o hide";
                        confirmpassword_sign_times3.className = "fa fa-times-circle-o hide";
                        confirmpassword_sign_times2.className = "fa fa-times-circle-o hide";
                        confirmpassword.style.borderColor = "#66FF66";
                        confirmpassword_hid.value = "";
                        confirmpassword_div.className = "hide";
                    }
                    else if (confirmpassword.value != "" && (confirmpassword.value != password.value && verdictvalue == "Weak"))
                    {
                        confirmpassword_sign_check.className = "fa fa-check-circle-o hide";
                        confirmpassword_sign_times.className = "fa fa-times-circle-o hide";
                        confirmpassword_sign_times2.className = "fa fa-times-circle-o show";
                        confirmpassword.style.borderColor = "#E85858";
                        confirmpassword_hid.value = "The confirm password you've entered doesn't match.";
                    }
                    else if (confirmpassword.value != "" && confirmpassword.value == password.value && verdictvalue == "Weak")
                    {  
                        confirmpassword_sign_check.className = "fa fa-check-circle-o hide";
                        confirmpassword_sign_times3.className = "fa fa-times-circle-o hide";
                        confirmpassword_sign_times2.className = "fa fa-times-circle-o hide";
                        confirmpassword_sign_times.className = "fa fa-times-circle-o hide";
                        confirmpassword.style.borderColor = "#E85858";
                        confirmpassword_hid.value = "";
                    }
                    else if (confirmpassword.value != "" && confirmpassword.value != password.value)
                    {
                        confirmpassword_sign_check.className = "fa fa-check-circle-o hide";
                        confirmpassword_sign_times.className = "fa fa-times-circle-o hide";
                        confirmpassword_sign_times2.className = "fa fa-times-circle-o show";
                        confirmpassword.style.borderColor = "#E85858";
                        confirmpassword_hid.value = "The confirm password you've entered doesn't match.";
                    }
                    else
                    {
                        confirmpassword_sign_check.className = "fa fa-check-circle-o hide";
                        confirmpassword_sign_times.className = "fa fa-times-circle-o show";
                        confirmpassword_sign_times2.className = "fa fa-times-circle-o hide";
                        confirmpassword.style.borderColor = "#E85858";
                        confirmpassword_hid.value = "Please enter your password confirmation.";
                    }

                    if (password_hid.value != "")
                    {
                        password_div.className = "show";
                        $("#password_message").html(password_hid.value);
                        ctr++;
                    }

                    if (confirmpassword_hid.value != "")
                    {
                        confirmpassword_div.className = "show";
                        $("#confirmpassword_message").html(confirmpassword_hid.value);
                        ctr++;
                    }

                    if (ctr != 0)
                    {
                        divalert.className = "alert alert-danger show";
                        ctr = 0;
                        $("#submit").text("SAVE CHANGES");
                        $("#submit").css("background-color", "#5bc0de");
                        $("#submit").css("width","50%"); 
                        $(".form-group :input").prop("readonly", false);
                    }
                    else if (ctr == 0)
                    {
                        $.post("resetpassword_save.php",
                            {
                                token:mytoken,
                                password:mypassword
                            },
                            function(data,status){
                                if (data=='done'){
                                    alert('hallooo');
                                }
                                //window.location.href = "sign-in_page.php";
                            }
                        );
                    }

                }, 1500);
            });

            $("#password").blur(function(){
                var password_hid = document.getElementById("password_hid");
                var password = document.getElementById("password");
                var verdict = document.getElementById("verdict");
                var verdictvalue = $("#verdict").text();
                var password_sign_times = document.getElementById("password_sign_times");
                var password_sign_times2 = document.getElementById("password_sign_times2");
                var password_sign_check = document.getElementById("password_sign_check");
                if (password.value != "" && verdictvalue != "Weak")
                {  
                    password_sign_check.className = "fa fa-check-circle-o show";
                    password_sign_times.className = "fa fa-times-circle-o hide";
                    password_sign_times2.className = "fa fa-times-circle-o hide";
                    password.style.borderColor = "#66FF66";
                    password_hid.value = "";
                }
                else if (verdictvalue == "Weak")
                {
                    password_sign_check.className = "fa fa-check-circle-o hide";
                    password_sign_times.className = "fa fa-times-circle-o hide";
                    password_sign_times2.className = "fa fa-times-circle-o show";
                    password.style.borderColor = "#E85858";
                    password_hid.value = "Your password is weak. Please try another one.";
                }
                else
                {
                    password_sign_check.className = "fa fa-check-circle-o hide";
                    password_sign_times.className = "fa fa-times-circle-o show";
                    password_sign_times2.className = "fa fa-times-circle-o hide";
                    password.style.borderColor = "#E85858";
                    password_hid.value = "Please enter your password.";
                }
            });
            
            $("#password").keydown(function(){
                var password_hid = document.getElementById("password_hid");
                var password = document.getElementById("password");
                var strtxt = document.getElementById("strtxt");
                var strbar = document.getElementById("strbar");
                var progressbar = document.getElementById("progressbar");
                var verdict = document.getElementById("verdict");
                var passwordvalue = $("#password").val();
                var password_sign_times = document.getElementById("password_sign_times");
                var password_sign_times2 = document.getElementById("password_sign_times2");
                var password_sign_check = document.getElementById("password_sign_check");
                
                $.post("check_password.php",
                    {
                        passwordvalue:passwordvalue
                    },
                    function(data,status){
                        if (password.value == "")
                        {
                            progressbar.className = "progress-bar hide";
                            $("#verdict").html("");
                            strbar.className = "progress hide";
                            strtxt.className = "hide";
                        }
                        else if (data == "1")
                        {
                            strbar.className = "progress show";
                            strtxt.className = "show";
                            progressbar.className = "progress-bar progress-bar-danger";
                            progressbar.style.width = "25%";
                            $("#verdict").html("Weak");
                        }
                        else if (data == "2")
                        {
                            strbar.className = "progress show";
                            strtxt.className = "show";
                            progressbar.className = "progress-bar progress-bar-warning";
                            progressbar.style.width = "50%";
                            $("#verdict").html("Fair");
                        }
                        else if (data == "3")
                        {
                            strbar.className = "progress show";
                            strtxt.className = "show";
                            progressbar.className = "progress-bar progress-bar-info";
                            progressbar.style.width = "75%";
                            $("#verdict").html("Strong");
                        }
                        else if (data == "4")
                        {
                            strbar.className = "progress show";
                            strtxt.className = "show";
                            progressbar.className = "progress-bar progress-bar-success";
                            progressbar.style.width = "100%";
                            $("#verdict").html("Very Strong");
                        }

                    });
            });
            
            $("#confirmpassword").blur(function(){
                var verdict = document.getElementById("verdict");
                var verdictvalue = $("#verdict").text();
                var confirmpassword_hid = document.getElementById("confirmpassword_hid");
                var password = document.getElementById("password");
                var confirmpassword = document.getElementById("confirmpassword");
                var confirmpassword_sign_times = document.getElementById("confirmpassword_sign_times");
                var confirmpassword_sign_times2 = document.getElementById("confirmpassword_sign_times2");
                var confirmpassword_sign_times3 = document.getElementById("confirmpassword_sign_times3");
                var confirmpassword_sign_check = document.getElementById("confirmpassword_sign_check");
                if (confirmpassword.value != "" && confirmpassword.value == password.value && verdictvalue != "Weak")
                {  
                    confirmpassword_sign_check.className = "fa fa-check-circle-o show";
                    confirmpassword_sign_times.className = "fa fa-times-circle-o hide";
                    confirmpassword_sign_times3.className = "fa fa-times-circle-o hide";
                    confirmpassword_sign_times2.className = "fa fa-times-circle-o hide";
                    confirmpassword.style.borderColor = "#66FF66";
                    confirmpassword_hid.value = "";
                }
                else if (confirmpassword.value != "" && (confirmpassword.value != password.value && verdictvalue == "Weak"))
                {
                    confirmpassword_sign_check.className = "fa fa-check-circle-o hide";
                    confirmpassword_sign_times.className = "fa fa-times-circle-o hide";
                    confirmpassword_sign_times2.className = "fa fa-times-circle-o show";
                    confirmpassword.style.borderColor = "#E85858";
                    confirmpassword_hid.value = "The confirm password you've entered doesn't match.";
                }
                else if (confirmpassword.value != "" && confirmpassword.value == password.value && verdictvalue == "Weak")
                {
                    confirmpassword_sign_check.className = "fa fa-check-circle-o hide";
                    confirmpassword_sign_times3.className = "fa fa-times-circle-o hide";
                    confirmpassword_sign_times2.className = "fa fa-times-circle-o hide";
                    confirmpassword_sign_times.className = "fa fa-times-circle-o hide";
                    confirmpassword.style.borderColor = "#E85858";
                    confirmpassword_hid.value = "";
                }
                else if (confirmpassword.value != "" && confirmpassword.value != password.value)
                {
                    confirmpassword_sign_check.className = "fa fa-check-circle-o hide";
                    confirmpassword_sign_times.className = "fa fa-times-circle-o hide";
                    confirmpassword_sign_times2.className = "fa fa-times-circle-o show";
                    confirmpassword.style.borderColor = "#E85858";
                    confirmpassword_hid.value = "The confirm password you've entered doesn't match.";
                }
                else
                {
                    confirmpassword_sign_check.className = "fa fa-check-circle-o hide";
                    confirmpassword_sign_times.className = "fa fa-times-circle-o show";
                    confirmpassword_sign_times2.className = "fa fa-times-circle-o hide";
                    confirmpassword.style.borderColor = "#E85858";
                    confirmpassword_hid.value = "Please enter your password confirmation.";
                }
            });
        });
    </script>

            <!-- scripts -->
        <script src="../js/bootstrap.min.js"></script>
        <!-- jQuery -->
        <script src="../bower_components/jquery/dist/jquery.min.js"></script>

        <!-- Bootstrap Core JavaScript -->
        <script src="../bower_components/bootstrap/dist/js/bootstrap.min.js"></script>

        

        <!-- Custom Theme JavaScript -->
        <script src="../dist/js/sb-admin-2.js"></script>
        
        <script>

            $('.tooltip-demo').tooltip({
                selector: "[data-toggle=tooltip]",
                container: "body"
            })
        </script>
        

</body>

</html>
