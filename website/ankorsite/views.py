from django.http import HttpResponse
from django.template import RequestContext, loader

def index(request):
    template = loader.get_template('index.html')
    context = RequestContext(request, {
        })
    return HttpResponse(template.render(context))

def examples(request):
    template = loader.get_template('examples.html')
    context = RequestContext(request, {
        })
    return HttpResponse(template.render(context))
