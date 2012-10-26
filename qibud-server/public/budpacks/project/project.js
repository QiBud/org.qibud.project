(function(){
    roles.packs.project.project = {

        inject_view: function( bud_identity, target, descriptor, state ) {

            var $view = $("#"+target);

            $view.append( "<h3>"+descriptor.description+"</h3>" );
            //$view.append(  "<h4>Role Descriptor</h4><pre>" + JSON.stringify( descriptor ) + "</pre>" );

            
            $view.append(  "<h4>Status</h4>" );
            $view.append( '<p>'+state.projectStatus+'</p>' );
            
            $view.append(  "<h4>Related mission</h4>" );
            $view.append( "<a href='/bud/"+state.mission+"' >this</a>" );
            
        }

    };
})();
