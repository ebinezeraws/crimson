<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <title>Document</title>
    <style>
   html,body{
            width: 100%;
            height: 100%;
        }
       #body{
           margin-top:2%;
           margin-left: 4%;
           padding:2%;
       }

       table{
           border-collapse: collapse;
       }
       table>thead{
           background-color: darkgray;
       }
       table>tbody{
           background-color: wheat;
       }
       </style>

</head>

<body>
    <section id="body" class="">
        <section id="mail" class="">
            <section id="mail-header">
                <h5>Hi ${user.username},</h5>
            </section>
            <section id="mail-subject">
                <p>Your Order - ${orderId} is delivered on ${dispatchedDate}.</p>
            </section>
            <section id="mail-body">
                <table id="order-items" border="1" class="table">
                    <thead class="">
                        <tr>
                            <th>Product Name</th>
                            <th>Unit Price</th>
                            <th>Quantity</th>
                            <th>Total Price</th>
                        </tr>
                    </thead>
                    <tbody class="bg-info">
                    <#list orderItems as orderItem>
                        <tr>
                            <td>${orderItem.product.productName}</td>
                            <td>${orderItem.unitPrice}</td>
                            <td>${orderItem.quantity}</td>
                            <td>${orderItem.totalPrice}</td>
                        </tr>
                        </#list>
                        <tfoot>
                            <tr><td colspan="3" class="text-center">Total Amount</td>
                            <td>${totalAmount}</td></tr>
                        </tfoot>
                    </tbody>
                </table>
            </section>
            <section id="mail-footer">
                <p>Thank you for showing intrest in our products. We are expecting your next order soon.</p>
            </section>
            <section id="mail-regards">
                <p>Regards,<br>
                Aadvi Team.</p>
            </section>
        </section>
    </section>
</body>

</html>