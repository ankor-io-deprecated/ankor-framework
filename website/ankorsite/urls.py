from django.conf.urls import patterns, include, url

from ankorsite import views

# Uncomment the next two lines to enable the admin:
# from django.contrib import admin
# admin.autodiscover()

urlpatterns = patterns('',
    # Examples:
    # url(r'^$', 'ankorsite.views.home', name='home'),
    # url(r'^ankorsite/', include('ankorsite.foo.urls')),

    # Uncomment the admin/doc line below to enable admin documentation:
    # url(r'^admin/doc/', include('django.contrib.admindocs.urls')),

    # Uncomment the next line to enable the admin:
    # url(r'^admin/', include(admin.site.urls)),
    url(r'^$', views.index, name='index'),
    url(r'^download', views.download, name='examples'),
    url(r'^examples', views.examples, name='examples'),
    url(r'^documentation', views.documentation, name='examples'),
    url(r'^contribute', views.contribute, name='examples'),
)
