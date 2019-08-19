<!DOCTYPE html>
<html lang="en">
<head>
  <title>Bootstrap Example</title>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  
  <style>
  
 
  
	
  </style>
</head>
<body>

  
    <div  style="background-color:#2E2EFE;height:100px">
    <p style=" color:white;
	  font-family:verdana;
	  margin-top:2%;
	  text-align:center;padding-top:2.4%;font-family: 'Karla'";>Invoice #${orderId}</p>
    </div><br><br>

	<p style="margin-left:23%;
	  font-family:verdana;
	  font-size:13px;
	  color:#000000;font-family: 'Karla'";>Dear ${user.userDetails.companyName}</p>
	<p style="margin-left:23%;
	  font-family:verdana;
	  font-size:13px;
	  color:#000000;font-family: 'Karla'">Thank you for your business. Your invoice can be viewed,printed and downloaded as PDF from<br>
	   <span>the link below. You can also choose to pay at online.<span></p>
 <br>
  
    <div style="height:388px;
	  border:1px solid #BDBDBD;
	  margin-left:23%;
	   margin-right:20%"> 
	   <h6 style="text-align:center; margin-top:5%;font-size:15px;font-family:'Karla';"><b>INVOICE AMOUNT</b></h6>
	   <h5 style="text-align:center;color:red;font-size:15px;font-family:'Karla'";>${totalAmount}</h5><br><hr>
	  
     
	  
<table align="center" >
<tr >
<td style="margin:5%;font-size:12px; ">
Invoice No
</td>
<td style="margin:5%;font-weight:bold;padding-left:20%;font-size:12px; ">
${orderId}
</td>
</tr>
<tr>
<td style="margin:5%;font-size:12px; ">
${orderedDate?date}
</td>
<td style="margin:5%;font-weight:bold;padding-left:20%;font-size:12px; ">
${deliveryDate?date}
</td>
</tr>
<tr>
<td style="margin:5%;font-size:12px; ">
due date
</td>
<td style="pmargin:5%;font-weight:bold;padding-left:20%;font-size:12px; ">
29/07/2017
</td>
</tr>
</table>
	
	<br>
	<button type="button" class="btn btn-success" style="margin-left:32%;
	 border-style:none;
	 width:40%;
	 padding:1%";><span style="font-size:10px;font-family:verdana;"><a href="http://crimsontrading.in/order/${orderId}">VIEW INVOICE</a></span></button><br><br><hr>
	<p style="font-size:13px;padding-left:5%";>Regards<br>
  Project Manager <br>
	 Crimson Trading </p>
	</div>
  </div>
  <br>
</body>
</html>