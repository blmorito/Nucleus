<?php session_start(); ?>

<!DOCTYPE html>
<html lang="en">

<head>

    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="description" content="">
    <meta name="author" content="">

    <title>Forgot your password?</title>

    <script src="../js/jquery-1.11.1.min.js"></script>
    <!-- Bootstrap Core CSS -->
    <link href="../bower_components/bootstrap/dist/css/bootstrap.min.css" rel="stylesheet">

    <!-- MetisMenu CSS -->
    <link href="../bower_components/metisMenu/dist/metisMenu.min.css" rel="stylesheet">

    <!-- Timeline CSS -->
    <link href="../dist/css/timeline.css" rel="stylesheet">

    <!-- Custom CSS -->
    <link href="../dist/css/sb-admin-2.css" rel="stylesheet">

    <!-- Morris Charts CSS -->
    <link href="../bower_components/morrisjs/morris.css" rel="stylesheet">

    <!-- Custom Fonts -->
    <link href="../bower_components/font-awesome/css/font-awesome.min.css" rel="stylesheet" type="text/css">

    <!-- HTML5 Shim and Respond.js IE8 support of HTML5 elements and media queries -->
    <!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
    <!--[if lt IE 9]>
        <script src="https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js"></script>
        <script src="https://oss.maxcdn.com/libs/respond.js/1.4.2/respond.min.js"></script>
    <![endif]-->

</head>

<body style = "background: #00253A">

    <div class = "container" style = "background: #00253A">
        <div class = "row" style ="padding-top:10px">
            <center>
                <img src = "../images/logofin.png" style = "padding-top:10px" height = "25%" width = "25%"> </img>
            </center>
        </div>
        <div class ="row" style = "padding-top: 15px">

            <div class = "col-md-3">
            </div>

            <div class = "well col-md-6" style = "background:#fff;padding-left:20px">
                <div class = "form-group"><br>
                    <?php
                        
                        if (isset($_SESSION["fromCheckToken"])){
                            if ($_SESSION["fromCheckToken"]== "true" && $_SESSION["tokenStatus"]=="expired"){
                                echo '<div style="padding-left:25px"><div class="alert alert-danger row col-xs-12" role="alert"><big><strong>Sorry! It seems that your password reset link has expired</strong>.</big></div></div>';
                                
                            }elseif ($_SESSION["tokenStatus"]=="none") {
                                echo '<div style="padding-left:25px"><div class="alert alert-danger row col-xs-12" role="alert"><big><strong>Sorry. It seems that the Reset Password Token has already been used or overwritten with a newer request</strong>. Please check your email or you can also request a new reset password link below</big></div></div>';
                            }
                        }
                    ?>
                    
                        <span id = "alertmessage" style = "font-size:20px; padding-left:25px"> <b> &nbsp;Can't sign-in? Forgot your password? </b></span><br>
                        <div class = "row">
                            <div class = "col-md-1">
                            </div>
                            <div class = "col-md-10">

                                <span id = "alertmessage" style = "font-size:14px">
                                    Enter the email address you provided at registration and we'll send you password reset instructions.
                                </span>
                            </div>
                            <div class = "col-md-1">
                            </div>
                        </div>
                        <br>
                </div>

                <div class = "row">
                    <div class = "col-md-1">
                    </div>
                    <div class = "col-md-10">
                        <div class="alert alert-danger hide" role="alert" id = "divalert">
                            <span class = "glyphicon glyphicon-exclamation-sign" style = "padding-left:10px"></span>&nbsp;
                            <span id = "email_message"></span>
                        </div>
                    </div>
                    <div class = "col-md-1">
                    </div>
                </div>

                <div class = "form" style = "padding-bottom:15px">
                    <div class = "row">
                        <div class = "col-xs-3" style = "text-align:right">
                            <img src = "../images/email.png" height = "78%" width = "78%"> </img>
                        </div>
                        <div class = "col-xs-8">
                           <input type ="input" class = "form-control" id = "email" placeholder="E-mail" style = "height: 40px">
                        </div>
                        <div class = "col-xs-1">
                        </div>
                    </div>
                    <div class = "row" style = "padding-top:15px">
                        <div class = "col-md-3">
                        </div>
                        <div class = "col-md-9">
                            <button class = "btn btn-info btn-md" id = "send" style = "border-radius:3px" title = "Click here to send reset instructions to your email.">
                                <!--span class = "fa fa-send"> </span-->
                                 &nbsp;&nbsp;SEND ME RESET INSTRUCTIONS&nbsp;&nbsp;
                            </button>
                        </div>
                    </div>
                        <hr>
                        <div id = "foot" class ="show">
                            Never mind, <u id = "clicks" style = "cursor:pointer;color:royalblue;font-size:16px" title = "Click here to go back to sign-in page.">send me back to the sign-in page</u>
                        </div>
                </div>
            </div>
            
            <div class = "col-md-3">
            </div>
        </div>
        <br><br><br>
    </div>

    <script>
        $("#clicks").click(function(){
            $("#email").val("");
            window.location.href = "sign-in_page.php";
        });
        $("#send").click(function(){
            $(this).text("Sending reset instructions to your email...");
            $(this).css("background-color", "#31708f");
            $(this).css("width","72%");
            var divalert = document.getElementById("divalert");
            var email_message = document.getElementById("email_message");
            var email = document.getElementById("email");
            var myemail = email.value;
            var foot = document.getElementById("foot");
            
                            $("#modalAdd").modal('show');
            setTimeout(function(){
                if (myemail != "")
                {
                    $.post("check_email.php",
                    {
                        emailvalue: myemail
                    },
                    function(data,status){

                        if(data=='found')
                        {
                            $.post("sendemail_forpassword.php",
                            {
                                email: myemail
                            },
                            function(data,status){
                                $("#email").val("");
                                $("#email_message").html("");
                                window.location.href = "sendemail_success.php";
                                divalert.className = "alert alert-danger hide";
                            });
                        }
                        else if(data=='invalid')
                        {
                            $("#email_message").html("Sorry, we couldn't find anyone with that email address.");
                            divalert.className = "alert alert-danger show";
                            $("#send").text("SEND ME RESET INSTRUCTIONS");
                            $("#send").css("background-color", "#5bc0de");
                            $("#send").css("width","66%");
                        }
                        else if(data == 'notfound')
                        {
                            $("#email_message").html("Sorry, we couldn't find anyone with that email address.");
                            divalert.className = "alert alert-danger show";
                            $("#send").text("SEND ME RESET INSTRUCTIONS");
                            $("#send").css("background-color", "#5bc0de");
                            $("#send").css("width","66%");
                        }
                    });
                }
                else
                {
                    $("#email_message").html("Sorry, we couldn't find anyone with that email address.");
                    divalert.className = "alert alert-danger show";
                    $("#send").text("SEND ME RESET INSTRUCTIONS");
                    $("#send").css("background-color", "#5bc0de");
                    $("#send").css("width","66%");
                }
            }, 1500);
        });
    </script>

    <script src="../js/jquery-1.11.1.min.js"></script>
    <!-- jQuery -->
    <script src="../bower_components/jquery/dist/jquery.min.js"></script>

    <!-- Bootstrap Core JavaScript -->
    <script src="../bower_components/bootstrap/dist/js/bootstrap.min.js"></script>

    <!-- Metis Menu Plugin JavaScript -->
    <script src="../bower_components/metisMenu/dist/metisMenu.min.js"></script>

    <!-- Morris Charts JavaScript -->
    <script src="../bower_components/raphael/raphael-min.js"></script>
    <script src="../bower_components/morrisjs/morris.min.js"></script>
    <script src="../js/morris-data.js"></script>

    <!-- Custom Theme JavaScript -->
    <script src="../dist/js/sb-admin-2.js"></script>

</body>

</html>
