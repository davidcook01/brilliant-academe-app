$('#recipeCarousel').carousel({
  interval: 10000
});

$('#recipeCarousel1').carousel({
  interval: 10000
});

$('#recipeCarousel2').carousel({
  interval: 10000
});


$('.carousel .carousel-item').each(function(){
    console.log('Cloning the carousel item');
    var next = $(this).next();
    if (!next.length) {
    next = $(this).siblings(':first');
    }
    next.children(':first-child').clone().appendTo($(this));
    
    for (var i=0;i<5;i++) {
        next=next.next();
        if (!next.length) {
        	next = $(this).siblings(':first');
      	}
        
        next.children(':first-child').clone().appendTo($(this));
      }
});