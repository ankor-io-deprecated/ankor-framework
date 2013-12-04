import os.path
from django.http import HttpResponse
from django.template import RequestContext, loader
from django.core.files import File

SITE_ROOT = os.path.dirname(os.path.realpath(__file__))

def index(request):
    template = loader.get_template('index.html')
    context = RequestContext(request, {
                'activeMenu': 'home',
        })
    return HttpResponse(template.render(context))

def download(request):
    template = loader.get_template('download.html')
    context = RequestContext(request, {
                'activeMenu': 'download',
        })
    return HttpResponse(template.render(context))

def tutorials(request, type, step):
    template = loader.get_template('tutorial/tutorial_' + type + '_' + step + '.html')

    path = open(SITE_ROOT + '/templates/tutorial/tutorial_fx_' + step + '.md', 'r')
    f = File(path)
    content = f.read()

    step = int(step)

    # FIXME
    next_step = int(step) + 1
    if next_step > 2:
        next_step = 2
    previous_step = int(step) - 1
    if previous_step < 0:
        previous_step = 0

    context = RequestContext(request, {
                'activeMenu': 'tutorials',
                'step': step,
                'previousStep': previous_step,
                'nextStep': next_step,
                'content': content
        })
    return HttpResponse(template.render(context))

def documentation(request):
    template = loader.get_template('documentation.html')
    context = RequestContext(request, {
                'activeMenu': 'documentation',
        })
    return HttpResponse(template.render(context))

def contribute(request):
    template = loader.get_template('contribute.html')
    context = RequestContext(request, {
                'activeMenu': 'contribute',
        })
    return HttpResponse(template.render(context))
