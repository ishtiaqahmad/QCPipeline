<h1>${project.name}</h1>

<p>${project.description}</p>
<span>${project.dateCreated}</span>

<h2>Upload Measurement Files here!</h2>

<div class="bordered">
    <g:uploadForm controller="project" action="addFile" id="${project?.id}" name="myUpload">
        select file: <input type="file" name="fileUpload"/>
        <g:submitButton name="submit" value="submit"/>
    </g:uploadForm>
</div>


<ul>
    <g:each in="${project.datas}" var="data">
        <li><g:link controller="data" action="view" id="${data.id}">${data.name}</g:link></li>
    </g:each>
</ul>
 <h2>Settings section</h2>
Current associated setting with project  ${project.name}
<select name="setting">
    <g:each in="${project.settings}" var="setting">
        <option value="${setting?.id}" >${setting?.name?.encodeAsHTML()}</option>
    </g:each>
</select>

<h4>Define New Project setting here!</h4>
<g:form name="createSetting" action="addSetting">
    Select Platform:
    <select name="platform">
        <g:each in="${config.platforms}" var="plat">
            <option value="${plat?.id}" >${plat?.encodeAsHTML()}</option>
        </g:each>
    </select>
    <br>
    Matrix Used:
    <select name="matrix">
        <g:each in="${config.matrixes}" var="matrix">
            <option value="${matrix?.id}" >${matrix?.encodeAsHTML()}</option>
        </g:each>
    </select>
    <br>
    Options (can be combined):
    <br>
    <select name="options" multiple="true">
        <g:each in="${ options=[
                                '0': 'DEBUG: Does not fill output column when finished',
                                '1': 'FORCE: recreate settings.xlsm',
                                '2': 'FORCE: recreate samplelist.xlsx',
                                '3': 'FORCE: reload data (removes mea\\data.mat)',
                                '4': 'OVERRULE: If codes are non NMC force type to validation (default: study/normal)',
                                '5': 'DEBUG: leave setting.xlsm open upon creation',
            ]}" var="opt">
            <option value="${opt.key}">${opt?.value.encodeAsHTML()}</option>
        </g:each>
    </select>
    <br>
    <g:submitButton name="submit" value="createProjectSetting"/>
</g:form>

