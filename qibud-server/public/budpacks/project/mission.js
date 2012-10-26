(function(){
    roles.packs.project.mission = {

        inject_view: function( bud_identity, target, descriptor, state ) {

            var $view = $("#"+target);

            $view.append( "<h3>"+descriptor.description+"</h3>" );
            //$view.append(  "<h4>Role Descriptor</h4><pre>" + JSON.stringify( descriptor ) + "</pre>" );

            
            $view.append(  "<h4>Status</h4>" );
            // mission status
            $view.append( '<p>'+state.missionStatus+'</p>' );
            
            $view.append(  "<h4>Projects</h4>" );
            //projects
            for(var p in state.projects)
            {
                $view.append("<a href='/bud/"+state.projects[p].id+"' >"+state.projects[p].title+"</a><br>")
            }
            
            //$view.append(  "<h4>Debug state</h4>" );
            //$view.append(  "<pre>" + JSON.stringify( state ) + "</pre>" );
        }

    };
})();
