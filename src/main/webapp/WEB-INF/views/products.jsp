<%@page isELIgnored="false" contentType="text/html; charset=ISO-8859-1"%>
<!doctype html>
<html lang="en">

<head>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Home | Crimson</title>
<%@include file="links.jsp"%>

<style type="text/css">
.product-img>img {
	height: 120px;
	width: 120px;
}

.quantity-box {
	display: flex;
	justify-content: center;
	margin-bottom: 0;
}

.input--filled {
	border-radius: 25px;
	background-color: #ef402b;
}
</style>

</head>

<body>

	<div class="grid-page">
		<!--page wrap-->

		<a href="javascript:" id="return-to-top"><i class="fa fa-angle-up"></i></a>

		<%@include file="header.jsp"%>

		<%@include file="searchmenu.jsp"%>


		<security:authorize access="isAnonymous() || hasRole('ROLE_USERS')">

			<!--discount-->
			<div class="container discount-banner padd-40">
				<div class="col-md-12 discount">
					<img src="${img}/index/banner.png" class="img-responsive" alt="" />
				</div>
			</div>

		</security:authorize>

		<!--special-offer-->
		<div class="container padd-60 special-offer">

			<div class="col-md-9 grid-list">
				<c:if test="${fn:length(products) le 0}">
				    NO PRODUCTS AVAILABLE
				</c:if>
				<c:if test="${status==true}">
					<span class="alert alert-success">Added to Cart</span>
				</c:if>
				<c:if test="${status==false}">
					<span class="alert alert-danger">Sorry error occurred .
						Product is not added to cart. Try again</span>
				</c:if>

				<c:if test="${disable==false}">
					<span class="alert alert-danger">Sorry error occurred . Try
						again</span>
				</c:if>
				
				<c:if test="${cartStatus==false}">
					<span class="alert alert-info">Cart is empty. Add Products to cart</span>
				</c:if>
				

				<div class="clearfix"></div>
				<div class='cinnamon'>
				<c:forEach items="${products}" var="prod" varStatus="vs">

					<div class="col-md-3 col-sm-5 mt-40">
						<div class="product">

							<div class="product-img">
								<a href="${contextPath}/prod_details/${prod.id}"
									class="product-href"></a> <img src="${prodimgs}/${prod.id}.jpg"
									alt="" class="img-responsive img-overlay" /> <img
									src="${prodimgs}/${prod.id}.jpg" alt="" class="img-responsive" />

								<div class="sale-heart-hover">
									<a href="#"><i class="flaticon-heart"></i></a>
								</div>
							</div>
							<div class="product-body">
								<p>
									<a href="${contextPath}/prod_details/${prod.id}">${prod.productName}</a>
								</p>
								<h4>Rs.${prod.price}</h4>
								<div class="product-hover">

									<c:set var="exists" value="false" />
									<c:set var="quant" value="0" />
									<c:set var="cartItemId" value="" />
									<c:forEach items="${cart.cartItems}" var="cartItem">
										<c:if test="${cartItem.product.id==prod.id}">
											<c:set var="exists" value="true" />
											<c:set var="quant" value="${cartItem.quantity}"></c:set>
											<c:set var="cartItemId" value="${cartItem.cartItemId}" />
										</c:if>

									</c:forEach>

									<c:if test="${exists}">

										<div class="detail-btm" style="">
											<div class="detail-row quantity-box mt-40">
												<div class="input--filled" id="input--filled">
													<button type="button" class="sub" id="sub">
														<i class="fa fa-minus" aria-hidden="true"></i>
													</button>
													<input type="text" id="${cartItemId}" class="prod_quantity"
														name="prod_quantity" value="${quant}" class="field">
													<button type="button" class="add" id="add">
														<i class="fa fa-plus" aria-hidden="true"></i>
													</button>

												</div>
											</div>
										</div>


									</c:if>
									<c:if test="${!exists}">
										<div class="add-cart-hover">
											<security:authorize access="hasRole('ROLE_USER')">
												<a href="${contextPath}/user/addtocart/${prod.id}"><h6>Add
														to cart</h6> <i class="flaticon-3-signs" aria-hidden="true"></i></a>
											</security:authorize>
											<security:authorize access="!hasRole('ROLE_USER')">
												<a href="${contextPath}/admin/editproduct/${prod.id}"><h6>Edit
														Product</h6> <i class="flaticon-3-signs" aria-hidden="true"></i></a>
											</security:authorize>
										</div>
									</c:if>


								</div>
							</div>

						</div>
					</div>

				</c:forEach>
				</div>

			</div>



			<div class="clearfix"></div>

		</div>

		<%@include file="footer.jsp"%>


		<security:authorize access="hasRole('ROLE_USER')">
			<script type="text/javascript">
				$(document)
						.ready(
								function() {
									$('.sub')
											.click(
													function() {
														var id = parseInt($(
																this).next()
																.attr('id'));
														var element = $(this)
																.next();
														var quant = parseInt(element
																.val());

														if (quant > 0) {
															element
																	.val(quant - 1);
															$
																	.get(
																			'/user/subcartitem/'
																					+ id,
																			function(
																					data,
																					error) {
																				if ((quant - 1) == 0) {
																					location
																					.reload(true);
																					
																				} else {
																					$(
																							'#cart_count')
																							.html(
																									parseInt($(
																											'#cart_count')
																											.html()) - 1);
																				}
																				
																				
																				

																			})
														}

													})

									$('.add')
											.click(
													function() {
														var id = parseInt($(
																this).prev()
																.attr('id'));
														var element = $(this)
																.prev();
														var quant = parseInt(element
																.val());
														element.val(quant + 1);
														$
																.get(
																		'/user/addcartitem/'
																				+ id,
																		function(
																				data,
																				error) {
																			if (data == true) {
																				$(
																						'#cart_count')
																						.html(
																								parseInt($(
																										'#cart_count')
																										.html()) + 1);
																			}

																		})
													})
								})
			</script>
		</security:authorize>
	</div>
</body>


</html>