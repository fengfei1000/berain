var root_id = "_app_root";
var root_path = "/";
$(function() {
  "use strict";
  $.getJSON("/namespace", function(data) {
        root_id = data._id;
        root_path = data.namespace;
        $("#Refresh").click(function() {
              refreshNode(root_id, false);
            });
        $.ajaxSetup({
              cache : false
            });
        $("#left").jstree({
              json_data : {
                data : [{
                      data : root_path,
                      state : "closed",
                      attr : {
                        id : "rain" + root_id,
                        _id : root_id,
                        value : "Berain Root",
                        key : "Berain Root"
                      }

                    }],
                ajax : {
                  url : "/next/nodes",
                  async : true,
                  data : function(data) {
                    // notify(data.attr?data.attr("id"):data);
                    return {
                      id : data.attr ? data.attr("_id") : root_id
                    };
                  },
                  success : function(d) {

                  }
                }
              },

              "plugins" : ["themes", "json_data", "ui", "hotkeys"],
              "themes" : {
                "theme" : "classic",
                "dots" : true,
                "icons" : true
              }
            }).bind("load_node.jstree", function(event, data) {
              // notify(data.rslt.obj.attr("id"));
              // event.preventDefault();
              // this.toggle_select(event.currentTarget );
            }).bind("click.jstree", function(event, data) {
              notify(data.rslt);
            }).bind("select_node.jstree", function(event, data) {

          $("#pkey").val(data.rslt.obj.attr("key"));
          $("#id").val(data.rslt.obj.attr("_id"));
          $("#pid").val(data.rslt.obj.attr("_id"));
          $("#pvalue").val(data.rslt.obj.attr("svalue"));
          $.getJSON("/get/node", {
                id : data.rslt.obj.attr("_id")
              }, function(json) {
                if (json.status && status != 'fail') {
                  $("#pkey").val(json.attr.key);
                  $("#id").val(json.attr._id);
                  $("#pid").val(json.attr._id);
                  $("#pvalue").val(json.attr.svalue);
                }
              });
            // notify(data.rslt.obj.attr("svalue"));
          });
      });
  $("#logout").click(function() {
        $("#logoutForm").submit();
      });
  $("#edit").click(function() {

        var id = $('#id').val();

        if (id == '') {
          notify("Please select node.");
          return false;
        }

        if (id == root_id) {
          notify("Root can't edit!")
          return false;
        }
        $.post("/edit/node", $("#form1").serialize(), function(data) {
              refreshNode(id, false);

              // $('#pkey').val("");
              // $('#pvalue').val("");
              $.gritter.add({
                    title : "Edit status",
                    text : data,
                    class_name : 'gritter-green',
                    sticky : false,
                    time : 2000
                  });

            }, "text");
      });
  $("#remove").click(function() {
        var pid = $('#id').val();

        if (pid == '') {
          notify("Please select node.");
          return false;
        }
        if (pid == root_id) {
          notify("Root can't remove!")
          return false;
        }
        $("#dialog").text("Are you sure?");
        $("#dialog").dialog({
              resizable : false,
              height : 140,
              modal : true,
              title : "Remove?",
              buttons : {
                Remove : function() {
                  $.post("/del/node", $("#form1").serialize(), function(data) {
                        refreshNode(pid, true);
                        $('#id').val("");
                        $('#pid').val("");
                        $('#pkey').val("");
                        $('#pvalue').val("");
                        $.gritter.add({
                              title : "Remove status",
                              text : data,
                              class_name : 'gritter-green',
                              sticky : false,
                              time : 2000
                            });
                      }, "text");
                  $(this).dialog("close");
                },
                Cancel : function() {
                  $(this).dialog("close");
                  $(this).html("");
                }
              }
            });
      });
  $("#form2").submit(function() {
        var pid = $('#pid').val();
        var key = $('#key').val();

        if (pid == '') {
          notify("Please select node.");
          return false;
        }
        if (key == '') {
          notify("Please type key & value.");
          return false;
        }
        $.post("/add/node", $(this).serialize(), function(data) {
              // notify(pid);
              refreshNode(pid, false);

              $('#key').val("");
              $('#value').val("");
              $.gritter.add({
                    title : "Add status",
                    text : data,
                    class_name : 'gritter-green',
                    sticky : false,
                    time : 2000
                  });
            }, "text");
        return false;
      });
    // refreshNode("_", false);
  });

function refreshNode(id, isParent) {
  var eid = "#rain" + id;
  var node = $.jstree._reference(eid);

  if (isParent) {
    // notify(JSON.stringify(node._get_node(), null, "\t"));
    node = node._get_parent(node._get_node());
    $.jstree._reference($(node.selector)).refresh();

  } else {
//    notify(node.is_open(eid));
    if (node.is_open(eid)) {
      node.refresh(eid);
    } else {
      if (id == root_id && node.is_leaf(eid)) {
        node.refresh(eid);
        node.load_node(eid, function() {
              node.open_node(eid);
            }, function() {
            });
      } else {
        node.open_node(eid);
        node.refresh(eid);
      }

    }

  }
}

function notify(msg) {
  $.gritter.add({
        title : "   Warning  ",
        text : msg + " ",
        class_name : 'gritter-red'
      });
}