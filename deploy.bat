@echo off
title �Զ�������ߡ����ү������
color 02
set project='test'
set curdir=D:\program\workspace\mybatis-generator-core-fix-master
cd %curdir%

REM ���ļ��豣��ΪANSI���룬������Windows��ִ��
REM java -cp .;jacob.jar test.CameraTest
REM rem------------��ʾ��ע�ͣ��൱��Java�е�//
REM @echo off------��ʾ�رջ��ԣ�������ʾ���м������������(Ĭ��DOS��̻��������ʾ����)
REM color----------����Ϊ02��ʾDOS���ڱ���Ϊ��ɫ��ǰ��(������)Ϊ��ɫ
REM xcopy----------�������/Y��ʾ�Զ�����ͬ���ļ�(�������DOS����ѯ��Y����N)
REM del------------ɾ���ļ����/S����ɾ��Ŀ¼��(��ɾ��Ŀ¼��Ŀ¼�µ�������Ŀ¼���ļ�)��/Q��ʾȷ��Ҫɾ��(����DOS����ʾ�û��Ƿ�ȷ��ɾ��)
REM rd-------------ɾ���ļ������/S��/Q������del����ĺ�����ͬ
REM ren------------�������ļ����÷���[ren 11.exe 22.exe]
REM echo ���������ļ���Ϊ��%project%
REM echo ���������ļ�����·��Ϊ��%curdir%
REM echo ���������ļ������̷�Ϊ��%partition%
REM echo ���������ļ����ڹ���Ϊ��%curdir%

echo ��ʼ���Maven���� =================================
echo %curdir%
REM xcopy %curdir%\pom.xml %curdir% /Y
REM call mvn clean package
call mvn package
echo Maven���̴����� =================================

echo;
echo ׼�������ʱ�ļ� =================================
REM rd %curdir%\.settings /S /Q
del %curdir%\pom.xml /Q
echo ��ʱ�ļ������� =================================

echo;
echo ��ʼ����War�������� =================================
if exist "%userprofile%\Desktop\" (
    REM ������Win7ϵͳ
    xcopy %curdir%\target\mybatis-generator-core-1.3.2-fix.jar %userprofile%\Desktop\ /Y
) else if exist "%userprofile%\����\" (
    REM ������XPϵͳ
    xcopy %curdir%\target\mybatis-generator-core-1.3.2-fix.jar %userprofile%\����\ /Y
)
echo War���Ѿ����������� =================================

echo;
pause