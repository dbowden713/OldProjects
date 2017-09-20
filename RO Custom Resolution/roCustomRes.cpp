#include "stdafx.h"
using namespace std;

int main(int argc, char* argv[]) {
	HKEY hKey;

	// Game Registry Location
	RegOpenKeyEx(HKEY_LOCAL_MACHINE, TEXT("Software\\Gravity Soft\\Ragnarok"), 0, KEY_ALL_ACCESS, &hKey);

	// Game options
	int width, height, fullScreen, curWidth, curHeight, curFullScreen;
	curWidth = curHeight = curFullScreen = 0;
	char fullScreenChoice;

	// Registry key
	char userName[255];
	DWORD dwType = REG_SZ;
	DWORD dwBufSize = sizeof(userName);

	// Read registry keys into program
	RegQueryValueExA(hKey, "ID", 0, &dwType, (BYTE*)userName, &dwBufSize);
	dwType = REG_DWORD;
	dwBufSize = sizeof(curWidth);
	RegQueryValueEx( hKey, TEXT("WIDTH"), 0, &dwType, (BYTE*)&curWidth, &dwBufSize);
	RegQueryValueEx( hKey, TEXT("HEIGHT"), 0, &dwType, (BYTE*)&curHeight, &dwBufSize);
	RegQueryValueEx( hKey, TEXT("ISFULLSCREEN"), 0, &dwType, (BYTE*)&curFullScreen, &dwBufSize);

	// Display current settings
	cout << "Ragnarok Custom Window Size" << endl;
	cout << "Welcome - " << userName << endl;
	cout << "\n=====================================\n" << endl;
	cout << "Current Resolution - " << curWidth << "x" << curHeight << endl;
	cout << "Full Screen - "; if(curFullScreen == 1) cout << "ENABLED" << endl; else cout << "DISABLED" << endl;
	cout << "\n=====================================\n" << endl;
	
	// Get new resolution from user
	cout << "\nNew Width: ";
	cin >> width;
	cout << "\nNew Height: ";
	cin >> height;
	cout << "\nFull Screen? (y/n) ";
	cin >> fullScreenChoice;

	// Input error check
	if(width < 0 || width > 3000 || height < 0 || height > 3000) {
		cout << "\nError: Invalid resolution/input, must be 0 - 3000\n";
		system("pause");
		return -1;
	}

	// Write new settings to registry
	if(fullScreenChoice == 'y' || fullScreenChoice == 'Y') fullScreen = 1;
	else fullScreen = 0;

	RegSetValueEx(hKey, TEXT("WIDTH"), NULL, REG_DWORD, (BYTE*)&width, sizeof(width));

	RegSetValueEx(hKey, TEXT("HEIGHT"), NULL, REG_DWORD, (BYTE*)&height, sizeof(height));

	RegSetValueEx(hKey, TEXT("ISFULLSCREENMODE"), NULL, REG_DWORD, (BYTE*)&fullScreen, sizeof(fullScreen));

	RegCloseKey(hKey);

	cout << "\nNew resolution " << width << "x" << height << " set!" << endl;

	if(fullScreen == 1) cout << "Full Screen ENABLED" << endl;
	else cout << "Full Screen DISABLED" << endl;

	system("pause");
	return 0;
}