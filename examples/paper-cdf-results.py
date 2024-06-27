import json
import requests
import numpy as np
import matplotlib.pyplot as plt

files = ["PetroleumSystem.json", "PetroleumSystem24.json", "PetroleumSystem48.json", "PetroleumSystem96.json", "PetroleumSystem192.json"]

URL = "http://faultflow-2point0.unifi.it/rest/system/clear"
requests.get(url=URL)

print("CDF analysis parameters:")
print("    time step: 2")
print("    time limit: 8000")
print()

for filename in files:
    with open(filename, "rb") as file:
        URL = "http://faultflow-2point0.unifi.it/rest/system/load"
        r = requests.post(url=URL, data=file, headers={"Content-Type": "application/json"})
        response = json.loads(r.text)
        system_uuid = response["uuid"]

        for componentType in response["componentTypes"]:
            if componentType['name'] == "GasDetectionSystem":
                error_uuid = componentType["errorModes"][0]["uuid"]
                break

    URL = "http://faultflow-2point0.unifi.it/rest/analysis/pyramisCDF"
    params = {"systemUUID": system_uuid, "errorModeUUID": error_uuid, "timeStep": 2, "timeLimit": 8000}
    r = requests.get(url=URL, params=params)
    response = json.loads(r.text)

    values = response['cdf']
    ticks = np.arange(0, response['timeLimit'] + response['timestep'], response['timestep'])
    plt.clf()
    plt.xlim(ticks[0], ticks[-1])
    plt.ylim(values[0], 1 + 0.01)
    plt.plot(ticks, values)
    plt.xlabel('time (d)')
    plt.ylabel('cdf')

    if filename == "PetroleumSystem.json":
        print("System with 12 basic events")
        plt.title("Top Event CDF - 12 basic events")
        plt.savefig("cdf12.pdf")
    elif filename == "PetroleumSystem24.json":
        print("System with 24 basic events")
        plt.title("Top Event CDF - 24 basic events")
        plt.savefig("cdf24.pdf")
    elif filename == "PetroleumSystem48.json":
        print("System with 48 basic events")
        plt.title("Top Event CDF - 48 basic events")
        plt.savefig("cdf48.pdf")
    elif filename == "PetroleumSystem96.json":
        print("System with 96 basic events")
        plt.title("Top Event CDF - 96 basic events")
        plt.savefig("cdf96.pdf")
    elif filename == "PetroleumSystem192.json":
        print("System with 192 basic events")
        plt.title("Top Event CDF - 192 basic events")
        plt.savefig("cdf192.pdf")

    print("Analysis time: " + str(response['elapsedAnalysisTime']) + "ms")

URL = "http://faultflow-2point0.unifi.it/rest/system/clear"
requests.get(url=URL)
