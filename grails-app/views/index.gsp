<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
  <title>NUMBER TRANSLATOR</title>
  <g:javascript library='jquery' />
</head>
<body>
    <div style="font-weight: bold; height:30px;">NUMBER TRANSLATOR</div>
    <div>
   <g:formRemote name="translation" on404="alert('not found!')" update="results"
              url="[controller: 'translate', action:'index']">
    Please enter a number to translate: <input name="input" type="text" />
    <g:submitButton name="submit" value="Go"/>
</g:formRemote>
</div>
<div id="results" style="font-weight: bold; color:#23238e;" ></div>
<r:layoutResources />
</body>
</html>