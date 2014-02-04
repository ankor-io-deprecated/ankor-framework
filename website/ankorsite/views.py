import os
import os.path
from django.http import HttpResponse
from django.template import RequestContext, loader
from django.core.files import File

SITE_ROOT = os.path.dirname(os.path.realpath(__file__))


def get_num_tutorials():
    path, dirs, files = os.walk(SITE_ROOT + '/templates/tutorial/steps/').next()
    return len(files)


num_tutorials = get_num_tutorials()


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


def tutorials_overview(request):
    template = loader.get_template('tutorials.html')
    context = RequestContext(request, {
        'activeMenu': 'tutorials',
    })
    return HttpResponse(template.render(context))


def get_titles():
    titles = [""] * get_num_tutorials()
    for x in range(0, len(titles)):
        path = open(SITE_ROOT + '/templates/tutorial/steps/tutorial_fx_' + str(x) + '.md', 'r')
        line = path.readline()
        try:
            titles[x] = line.replace('### ', '')
        except Exception:
            print("Tutorials needs to start with '### '")
    return titles

tutorial_titles = get_titles()

def tutorials(request, type, step):
    template = loader.get_template('tutorial/tutorial_' + type + '.html')

    path = open(SITE_ROOT + '/templates/tutorial/steps/tutorial_fx_' + step + '.md', 'r')
    f = File(path)
    content = f.read()

    step = int(step)

    next_step = int(step) + 1
    if next_step > num_tutorials - 1:
        next_step = num_tutorials - 1
    previous_step = int(step) - 1
    if previous_step < 0:
        previous_step = 0

    context = RequestContext(request, {
        'activeMenu': 'tutorials',
        'step': step,
        'tutorial_titles': tutorial_titles,
        'tutorial_title': tutorial_titles[step],
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
