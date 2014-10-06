from django.conf.urls import patterns, include, url

import views

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
    url(r'^$', views.index),
    url(r'^download', views.download),
    url(r'^tutorials/(?P<type>[a-z]+)/(?P<step>\d+)', views.tutorials),
    url(r'^tutorials/(?P<type>[a-z]+)', views.tutorials_detail),
    url(r'^tutorials', views.tutorials_overview),
    url(r'^documentation/stateless', views.stateless),
    url(r'^documentation', views.documentation),
    url(r'^manual/(?P<version>[0-9\.]+)', views.manual),
    url(r'^spidoc/(?P<version>[0-9\.]+)', views.spidoc),
    url(r'^contribute', views.contribute),
    url(r'^javadoc/(?P<apiurl>.*)', views.javadoc),
    url(r'^.*', views.index),
)
