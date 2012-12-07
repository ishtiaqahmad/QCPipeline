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
<div style="margin: 5px; width: 40%; border: 1px dotted #FF0000">
<g:form name="prepare" action="prepareQCReport" controller="project" id="${project?.id}">
    Current associated setting with project  ${project.name}
    <g:select from="${project?.settings}" name="setting" value="${setting?.id}" optionValue="name" optionKey="id"/>
    <g:submitButton name="submit" value="prepare QC Report"/>
</g:form>
</div>

<h4>Define New Project setting here!</h4>
<g:form name="createSetting" action="addSetting" controller="project" id="${project?.id}">
    Name: <g:field type="text" name="name" required="true" value="Default Setting"/>
    <br>
    Select Platform:
    <g:select from="${config.platforms}" name="platform" value="${platform?.id}" optionKey="id"/>
    <br>
    Matrix Used:
    <g:select from="${config.matrixes}" name="matrix" value="${matrix?.id}" optionKey="id"/>
    <br>
    Stabilizer
    <g:select from="${config.additiveStabilizers}" name="additive" value="${additive?.id}" optionKey="id"/>
    <br>
    Options (can be combined):
    <br>
    // need to be moved to Congif.groovy
    <g:select from="${options=[
            '0': 'DEBUG: Does not fill output column when finished',
            '1': 'FORCE: recreate settings.xlsm',
            '2': 'FORCE: recreate samplelist.xlsx',
            '3': 'FORCE: reload data (removes mea\\data.mat)',
            '4': 'OVERRULE: If codes are non NMC force type to validation (default: study/normal)',
            '5': 'DEBUG: leave setting.xlsm open upon creation',
    ]}" name="options" multiple="true" value="${options?.key}" optionKey="key" optionValue="value"/>
    <br>
    <g:submitButton name="submit" value="createProjectSetting"/>
</g:form>

