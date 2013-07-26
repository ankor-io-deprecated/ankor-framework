from django.http import HttpResponse
from django.template import RequestContext, loader

def index(request):
    template = loader.get_template('index.html')
    context = RequestContext(request, {
                'home': 'active',
        })
    return HttpResponse(template.render(context))

def download(request):
    template = loader.get_template('download.html')
    context = RequestContext(request, {
                'download': 'active',
        })
    return HttpResponse(template.render(context))

def examples(request):
    template = loader.get_template('examples.html')
    context = RequestContext(request, {
                'examples': 'active',
        })
    return HttpResponse(template.render(context))

def documentation(request):
    template = loader.get_template('documentation.html')
    context = RequestContext(request, {
                'documentation': 'active',
        })
    return HttpResponse(template.render(context))

def contribute(request):
    template = loader.get_template('contribute.html')
    context = RequestContext(request, {
                'contribute': 'active',
        })
    return HttpResponse(template.render(context))
